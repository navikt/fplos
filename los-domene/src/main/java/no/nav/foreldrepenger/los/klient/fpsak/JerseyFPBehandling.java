package no.nav.foreldrepenger.los.klient.fpsak;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink.HttpMethod.POST;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Jersey
@Dependent
public class JerseyFPBehandling extends AbstractJerseyRestClient implements FPBehandling {

    private static final String BEHANDLING_ID = "behandlingId";

    private final URI baseUri;

    @Inject
    public JerseyFPBehandling(@KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public BehandlingDto hentUtvidetBehandlingDto(String id) {
        return invoke(id);
    }

    @Override
    public <T> Optional<T> hentFraResourceLink(ResourceLink resourceLink, Class<T> clazz) {
        if (POST.equals(resourceLink.getType())) {
            Optional.ofNullable(invoke(client.target(baseUri)
                    .path(resourceLink.getHref().toString())
                    .request(APPLICATION_JSON_TYPE)
                    .buildPost(json(resourceLink.getRequestPayload())), clazz));
        }
        return Optional.ofNullable(invoke(client.target(baseUri)
                .path(resourceLink.getHref().toString())
                .request(APPLICATION_JSON_TYPE)
                .buildGet(), clazz));
    }

    private BehandlingDto invoke(String id) {
        return invoke(client.target(baseUri)
                .path(FPSAK_BEHANDLINGER)
                .queryParam(BEHANDLING_ID, id)
                .request(APPLICATION_JSON_TYPE)
                .buildGet(), BehandlingDto.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }

}
