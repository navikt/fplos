package no.nav.foreldrepenger.los.web.app.tjenester.abac;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.vedtak.felles.integrasjon.rest.*;
import no.nav.vedtak.sikkerhet.abac.pipdata.AbacPipDto;
import no.nav.vedtak.util.LRUCache;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPSAK)
public class ForeldrepengerPipKlient {

    private static final String FPSAK_PIP_ENDPOINT = "/api/pip/pipdata-for-behandling-appintern";

    private static final int PIP_CACHE_SIZE = 500;
    private static final int PIP_CACHE_TIMEOUT_MILLIS = 30000;

    private final RestClient restClient;
    private final RestConfig restConfig;

    private LRUCache<BehandlingId, AbacPipDto> pipCache;

    public ForeldrepengerPipKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.pipCache = new LRUCache<>(PIP_CACHE_SIZE, PIP_CACHE_TIMEOUT_MILLIS);
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
        var pipUri = UriBuilder.fromUri(restConfig.fpContextPath())
            .path(FPSAK_PIP_ENDPOINT)
            .queryParam("behandlingUuid", behandlingId.toString())
            .build();
        var request = RestRequest.newGET(pipUri, restConfig);
        return restClient.sendReturnOptional(request, AbacPipDto.class).orElseThrow(IllegalStateException::new);

    }
}
