package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.net.URI;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, application = FpApplication.FPSAK)
public class FpsakBehandlingKlient implements BehandlingKlient {

    private final RestClient klient;
    private final RestConfig restConfig;
    private final URI baseUri;

    public FpsakBehandlingKlient() {
        this.klient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.baseUri = restConfig.fpContextPath();
    }

    @Override
    public LosBehandlingDto hentLosBehandlingDto(UUID uuid) {
        var target = UriBuilder.fromUri(baseUri).path("/api/los/los-behandling").queryParam("uuid", uuid.toString()).build();
        return klient.send(RestRequest.newGET(target, restConfig), LosBehandlingDto.class);
    }

    @Override
    public LosFagsakEgenskaperDto hentLosFagsakEgenskaperDto(Saksnummer saksnummer) {
        var target = UriBuilder.fromUri(baseUri).path("/api/los/los-egenskap").queryParam("saksnummer", saksnummer.value()).build();
        return klient.send(RestRequest.newGET(target, restConfig), LosFagsakEgenskaperDto.class);
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }

}
