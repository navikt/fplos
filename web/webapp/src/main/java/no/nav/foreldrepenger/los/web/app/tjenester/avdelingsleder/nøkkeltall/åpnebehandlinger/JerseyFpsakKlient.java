package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class JerseyFpsakKlient extends AbstractJerseyOidcRestClient {
    private static final String DEFAULT_FPSAK_BASE_URI = "http://fpsak";
    private static final String ÅPNE_BEHANDLING_PATH = "/fpsak/api/los-nokkeltall/behandlinger-ventestatus";
    private static final Logger LOG = LoggerFactory.getLogger(JerseyFpsakKlient.class);

    private URI endpoint;

    @Inject
    public JerseyFpsakKlient(@KonfigVerdi(value = "fpsak.url", defaultVerdi = DEFAULT_FPSAK_BASE_URI) URI endpoint) {
        this.endpoint = endpoint;
    }

    public JerseyFpsakKlient() {
    }

    public List<NøkkeltallBehandlingVentestatusDto> hentBehandlingVentestatusNøkkeltall() {
        LOG.info("Henter liste med åpne behandlinger");
        var resultat = client.target(endpoint)
                .path(ÅPNE_BEHANDLING_PATH)
                .request(APPLICATION_JSON_TYPE)
                .get(Response.class)
                .readEntity(new GenericType<List<NøkkeltallBehandlingVentestatusDto>>(){});
        LOG.info("Fant liste med åpne behandlinger OK");
        return resultat;
    }

}
