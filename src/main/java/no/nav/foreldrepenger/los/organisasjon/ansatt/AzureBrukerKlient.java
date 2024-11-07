package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.net.URI;
import java.util.UUID;

import jakarta.enterprise.context.Dependent;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPTILGANG)
public class AzureBrukerKlient {

    private final RestClient restClient;
    private final RestConfig restConfig;

    private final URI identUri;
    private final URI uuidUri;

    public AzureBrukerKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.identUri = UriBuilder.fromUri(restConfig.fpContextPath()).path("/api/bruker/profil/navident").build();
        this.uuidUri = UriBuilder.fromUri(restConfig.fpContextPath()).path("/api/bruker/profil/uid").build();
    }

    public BrukerProfilResponse brukerProfil(String ident) {
        var request = RestRequest.newPOSTJson(new ProfilIdentRequest(ident), identUri, restConfig);
        return restClient.send(request, BrukerProfilResponse.class);
    }

    public BrukerProfilResponse brukerProfil(UUID saksbehandler) {
        var request = RestRequest.newPOSTJson(new ProfilUidRequest(saksbehandler), uuidUri, restConfig);
        return restClient.send(request, BrukerProfilResponse.class);
    }

    record ProfilUidRequest(@NotNull UUID uid) { }

    public record ProfilIdentRequest(@NotNull String ident) { }

    public record BrukerProfilResponse(UUID uid, String ident, String fornavnEtternavn, String ansattVedEnhetId) {}
}
