package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.net.URI;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPTILGANG)
public class AzureBrukerKlient {

    private final RestClient restClient;
    private final RestConfig restConfig;

    private final URI uri;

    public AzureBrukerKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.uri = UriBuilder.fromUri(restConfig.fpContextPath()).path("/api/bruker/profil").build();
    }

    public BrukerProfilResponse brukerProfil(String ident) {
        var request = RestRequest.newPOSTJson(new BrukerProfilRequest(ident.trim()), UriBuilder.fromUri(uri).build(), restConfig);
        return restClient.send(request, BrukerProfilResponse.class);
    }

    record BrukerProfilRequest(@NotNull String ident) {}

    public record BrukerProfilResponse(String ident, String navn, String fornavnEtternavn, String epost, String ansattVedEnhetId) {}
}
