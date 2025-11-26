package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.fpsakklient;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingFørsteUttakDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingVentefristUtløperDto;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, application = FpApplication.FPSAK)
public class FpsakKlientImpl implements FpsakKlient {
    private static final String ÅPNE_BEHANDLING_PATH = "/api/los/los-nokkeltall";
    private static final String FRISTUTLOP_PATH = "/api/los/los-fristutlop-uke";
    private static final Logger LOG = LoggerFactory.getLogger(FpsakKlientImpl.class);

    private final RestClient klient;
    private final RestConfig restConfig;
    private final URI baseUriUttak;
    private final URI baseUriFrist;

    public FpsakKlientImpl() {
        this.klient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.baseUriUttak = UriBuilder.fromUri(restConfig.fpContextPath()).path(ÅPNE_BEHANDLING_PATH).build();
        this.baseUriFrist = UriBuilder.fromUri(restConfig.fpContextPath()).path(FRISTUTLOP_PATH).build();
    }

    public List<NøkkeltallBehandlingFørsteUttakDto> hentBehandlingFørsteUttakNøkkeltall() {
        LOG.info("Henter liste med åpne behandlinger");
        var request = RestRequest.newGET(baseUriUttak, restConfig);
        var resultat = klient.send(request, NøkkeltallBehandlingFørsteUttakDto[].class);
        LOG.info("Fant liste med åpne behandlinger OK");
        return Arrays.asList(resultat);
    }

    public List<NøkkeltallBehandlingVentefristUtløperDto> hentVentefristerNøkkeltall() {
        LOG.info("Henter liste med åpne behandlinger");
        var request = RestRequest.newGET(baseUriFrist, restConfig);
        var resultat = klient.send(request, NøkkeltallBehandlingVentefristUtløperDto[].class);
        LOG.info("Fant liste med åpne behandlinger OK");
        return Arrays.asList(resultat);
    }

}
