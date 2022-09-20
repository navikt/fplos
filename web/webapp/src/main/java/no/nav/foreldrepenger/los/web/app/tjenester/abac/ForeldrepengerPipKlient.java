package no.nav.foreldrepenger.los.web.app.tjenester.abac;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.sikkerhet.abac.pipdata.AbacPipDto;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.STS_CC, application = FpApplication.FPSAK)
public class ForeldrepengerPipKlient {

    private static final String FPSAK_PIP_ENDPOINT = "/api/pip/pipdata-for-behandling-appintern";

    private static final int PIP_CACHE_SIZE = 500;
    private static final int PIP_CACHE_TIMEOUT_MILLIS = 30000;

    private RestClient restClient;
    private URI fpsakBaseUrl;

    private LRUCache<BehandlingId, AbacPipDto> pipCache;

    @Inject
    public ForeldrepengerPipKlient(RestClient restClient) {
        this.restClient = restClient;
        this.fpsakBaseUrl = RestConfig.contextPathFromAnnotation(ForeldrepengerPipKlient.class);
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
        var pipUri = UriBuilder.fromUri(fpsakBaseUrl).path(FPSAK_PIP_ENDPOINT)
                .queryParam("behandlingUuid", behandlingId.toString())
                .build();
        var request = RestRequest.newGET(pipUri, TokenFlow.STS_CC, null);
        return restClient.sendReturnOptional(request, AbacPipDto.class)
                .orElseThrow(IllegalStateException::new);

    }
}
