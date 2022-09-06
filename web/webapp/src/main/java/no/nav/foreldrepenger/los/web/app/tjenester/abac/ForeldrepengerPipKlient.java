package no.nav.foreldrepenger.los.web.app.tjenester.abac;

import java.net.URISyntaxException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.vedtak.felles.integrasjon.rest.SystemUserOidcRestClient;
import no.nav.vedtak.sikkerhet.abac.pipdata.AbacPipDto;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class ForeldrepengerPipKlient {

    private static final String FPSAK_PIP_ENDPOINT = "/fpsak/api/pip/pipdata-for-behandling-appintern";

    private static final int PIP_CACHE_SIZE = 500;
    private static final int PIP_CACHE_TIMEOUT_MILLIS = 30000;

    private SystemUserOidcRestClient systemUserOidcRestClient;
    private String fpsakBaseUrl;

    private LRUCache<BehandlingId, AbacPipDto> pipCache;

    @Inject
    public ForeldrepengerPipKlient(SystemUserOidcRestClient systemUserOidcRestClient,
                                   @KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") String fpsakUrl) {
        this.systemUserOidcRestClient = systemUserOidcRestClient;
        this.fpsakBaseUrl = fpsakUrl;
        this.pipCache = new LRUCache<>(PIP_CACHE_SIZE, PIP_CACHE_TIMEOUT_MILLIS);
    }

    ForeldrepengerPipKlient() {
        //CDI
    }

    public AbacPipDto hentPipdataForBehandling(BehandlingId behandlingId) {
        var cached = pipCache.get(behandlingId);
        if (cached != null) {
            return cached;
        }
        var ny = hentFraFpsak(behandlingId);
        pipCache.put(behandlingId, ny);
        return ny;
    }

    private AbacPipDto hentFraFpsak(BehandlingId behandlingId) {
        try {
            var pipUriBuilder = new URIBuilder(fpsakBaseUrl + FPSAK_PIP_ENDPOINT);
            pipUriBuilder.setParameter("behandlingUuid", behandlingId.toString());
            return systemUserOidcRestClient.getReturnsOptional(pipUriBuilder.build(), AbacPipDto.class)
                    .orElseThrow(IllegalStateException::new);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
