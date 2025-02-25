package no.nav.foreldrepenger.los.server.abac;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.sikkerhet.abac.pipdata.PipAktørId;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPSAK)
public class ForeldrepengerPipKlient {

    private static final String FPSAK_PIP_ENDPOINT = "/aktoer-for-behandling";

    private static final int PIP_CACHE_SIZE = 500;
    private static final int PIP_CACHE_TIMEOUT_MILLIS = 30000;

    private final RestClient restClient;
    private final RestConfig restConfig;

    private final LRUCache<BehandlingId, Set<PipAktørId>> pipCache;

    public ForeldrepengerPipKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.pipCache = new LRUCache<>(PIP_CACHE_SIZE, PIP_CACHE_TIMEOUT_MILLIS);
    }

    public Set<PipAktørId> hentPipdataForBehandling(BehandlingId behandlingId) {
        var cached = pipCache.get(behandlingId);
        if (cached != null) {
            return cached;
        }
        var ny = hentFraFpsak(behandlingId);
        pipCache.put(behandlingId, new HashSet<>(ny));
        return pipCache.get(behandlingId);
    }

    private List<PipAktørId> hentFraFpsak(BehandlingId behandlingId) {
        var pipUri = UriBuilder.fromUri(restConfig.fpContextPath())
            .path(FPSAK_PIP_ENDPOINT)
            .queryParam("behandlingUuid", behandlingId.toString())
            .build();
        var request = RestRequest.newGET(pipUri, restConfig);
        return restClient.sendReturnList(request, PipAktørId.class);

    }
}
