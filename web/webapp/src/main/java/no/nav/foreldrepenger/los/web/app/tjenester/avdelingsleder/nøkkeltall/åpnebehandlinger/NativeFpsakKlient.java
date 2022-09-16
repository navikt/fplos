package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestCompact;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;

@Dependent
@NativeClient
@RestClientConfig(endpointProperty = "fpsak.url", endpointDefault = "http://fpsak") // TODO - ønsker bruke appliaction = men ressurslenkene begynner med /<fpapp>/api/...
public class NativeFpsakKlient extends AbstractJerseyOidcRestClient implements FpsakKlient {
    private static final String DEFAULT_FPSAK_BASE_URI = "http://fpsak";
    private static final String ÅPNE_BEHANDLING_PATH = "/fpsak/api/los-nokkeltall/behandlinger-ventestatus";
    private static final Logger LOG = LoggerFactory.getLogger(NativeFpsakKlient.class);

    private final RestCompact klient;
    private final URI baseUri;

    @Inject
    public NativeFpsakKlient(RestCompact klient) {
        this.klient = klient;
        this.baseUri = UriBuilder.fromUri(RestConfig.endpointFromAnnotation(NativeFpsakKlient.class)).path(ÅPNE_BEHANDLING_PATH).build();
    }

    public List<NøkkeltallBehandlingVentestatusDto> hentBehandlingVentestatusNøkkeltall() {
        LOG.info("Henter liste med åpne behandlinger");
        var resultat = klient.getValue(NativeFpsakKlient.class, baseUri, NøkkeltallBehandlingVentestatusDto[].class);
        LOG.info("Fant liste med åpne behandlinger OK");
        return Arrays.asList(resultat);
    }

}
