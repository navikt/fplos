package no.nav.foreldrepenger.los.server.abac;

import jakarta.enterprise.context.Dependent;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.vedtak.felles.integrasjon.rest.*;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPTILGANG)
public class TilgangFilterKlient {

    private final URI filterUri;
    private final RestClient klient;
    private final RestConfig restConfig;

    protected TilgangFilterKlient() {
        this(RestClient.client());
    }

    protected TilgangFilterKlient(RestClient restClient) {
        this.klient = restClient;
        this.restConfig = RestConfig.forClient(this.getClass());
        this.filterUri = UriBuilder.fromUri(restConfig.fpContextPath())
            .path("/api/populasjon/filtersaksnummer")
            .build();
    }

    public Set<String> tilgangFilterSaker(UUID ansattOid, Set<String> saksnummer) {
        var request = new TilgangFilterKlient.FilterSaksnummerRequest(ansattOid, saksnummer);
        var rrequest = RestRequest.newPOSTJson(request, filterUri, restConfig);
        var resultat = klient.send(rrequest, TilgangFilterKlient.FilterResponse.class);
        var resultatHarTilgang = Optional.ofNullable(resultat.harTilgang()).orElseGet(Set::of);
        return saksnummer.stream()
            .filter(resultatHarTilgang::contains)
            .collect(Collectors.toSet());
    }


    public record FilterSaksnummerRequest(@NotNull UUID ansattOid, Set<String> saker) { }

    // Hvilke av nøklene i request som den ansatte har tilgang til
    public record FilterResponse(Set<String> harTilgang) {}

}
