package no.nav.foreldrepenger.los.klient.fpsak;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink.HttpMethod.POST;
import static no.nav.vedtak.util.env.ConfidentialMarkerFilter.CONFIDENTIAL;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Jersey
@Dependent
public class JerseyForeldrepengerBehandling extends AbstractJerseyOidcRestClient implements ForeldrepengerBehandling {

    private final URI baseUri;

    @Inject
    public JerseyForeldrepengerBehandling(@KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public BehandlingDto hentUtvidetBehandlingDto(String id) {
        LOG.trace("Henter behandling for {}", id);
        var res = invoke(id);
        LOG.info("Hentet behandling for {} OK", id);
        return res;
    }

    @Override
    public <T> Optional<T> hentFraResourceLink(ResourceLink link, Class<T> clazz) {
        var target = client.target(baseUri)
                .path(link.getHref().getRawPath());
        target = QueryUtil.addQueryParams(link.getHref(), target);

        LOG.info(CONFIDENTIAL, "Henter fra URL {}", target.getUri());
        if (POST.equals(link.getType())) {
            var res = Optional.ofNullable(invoke(target
                    .request(APPLICATION_JSON_TYPE)
                    .buildPost(json(link.getRequestPayload())), clazz));
            LOG.info(CONFIDENTIAL, "Hentet med POST fra URL {} OK", target.getUri());
            return res;
        }
        var res = Optional.ofNullable(invoke(target
                .request(APPLICATION_JSON_TYPE)
                .buildGet(), clazz));
        LOG.info(CONFIDENTIAL, "Hentet med GET fra URL {} OK", target.getUri());
        return res;
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
