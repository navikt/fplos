package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.core.UriBuilder;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPSAK)
public class AktørPipKlient {

    private static final String PIP_PATH = "/api/pip/aktoer-for-sak";

    private final RestClient restClient;
    private final RestConfig restConfig;

    public AktørPipKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(AktørPipKlient.class);
    }

    public List<String> hentAktørIderSomString(Saksnummer saksnummer) {
        var uri = UriBuilder.fromUri(restConfig.fpContextPath())
                .path(PIP_PATH)
                .queryParam("saksnummer", saksnummer.value())
                .build();
        return restClient.sendReturnList(RestRequest.newGET(uri, restConfig), String.class);
    }
}
