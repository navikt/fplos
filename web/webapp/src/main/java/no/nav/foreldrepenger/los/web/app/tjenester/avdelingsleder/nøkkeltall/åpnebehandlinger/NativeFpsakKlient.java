package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@NativeClient
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, application = FpApplication.FPSAK)
public class NativeFpsakKlient implements FpsakKlient {
    private static final String ÅPNE_BEHANDLING_PATH = "/api/los-nokkeltall/behandlinger-ventestatus";
    private static final Logger LOG = LoggerFactory.getLogger(NativeFpsakKlient.class);

    private RestClient klient;
    private URI baseUri;

    public NativeFpsakKlient() {
        // CDI
    }

    @Inject
    public NativeFpsakKlient(RestClient klient) {
        this.klient = klient;
        this.baseUri = UriBuilder.fromUri(RestConfig.contextPathFromAnnotation(NativeFpsakKlient.class)).path(ÅPNE_BEHANDLING_PATH).build();
    }

    public List<NøkkeltallBehandlingVentestatusDto> hentBehandlingVentestatusNøkkeltall() {
        LOG.info("Henter liste med åpne behandlinger");
        var request = RestRequest.newGET(baseUri, NativeFpsakKlient.class);
        var resultat = klient.send(request, NøkkeltallBehandlingVentestatusDto[].class);
        LOG.info("Fant liste med åpne behandlinger OK");
        return Arrays.asList(resultat);
    }

}
