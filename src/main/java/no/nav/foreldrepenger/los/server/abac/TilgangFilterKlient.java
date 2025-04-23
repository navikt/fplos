package no.nav.foreldrepenger.los.server.abac;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

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

    public Set<Saksnummer> tilgangFilterSaker(UUID ansattOid, List<Oppgave> oppgaver) {
        var saksnummer = oppgaver.stream().map(Oppgave::getSaksnummer).map(Saksnummer::getVerdi).collect(Collectors.toSet());
        var request = new TilgangFilterKlient.FilterSaksnummerRequest(ansattOid, saksnummer);
        var rrequest = RestRequest.newPOSTJson(request, filterUri, restConfig);
        var resultat = klient.send(rrequest, TilgangFilterKlient.FilterResponse.class);
        var resultatHarTilgang = Optional.ofNullable(resultat.harTilgang()).orElseGet(Set::of);
        return saksnummer.stream()
            .filter(resultatHarTilgang::contains)
            .map(Saksnummer::new)
            .collect(Collectors.toSet());
    }


    public record FilterSaksnummerRequest(@NotNull UUID ansattOid, Set<String> saker) { }

    // Hvilke av nøklene i request som den ansatte har tilgang til
    public record FilterResponse(Set<String> harTilgang) {}

}
