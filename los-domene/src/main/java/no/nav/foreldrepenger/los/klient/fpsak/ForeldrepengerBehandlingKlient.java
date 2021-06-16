package no.nav.foreldrepenger.los.klient.fpsak;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

@ApplicationScoped
@Deprecated
@Dependent
/**
 *
 * @see JerseyFPBehandling
 *
 */
public class ForeldrepengerBehandlingKlient implements FPBehandling {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerBehandlingKlient.class);

    private final OidcRestClient oidcRestClient;
    private final String fpsakBaseUrl;

    @Inject
    public ForeldrepengerBehandlingKlient(OidcRestClient oidcRestClient,
            @KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") String fpsakUrl) {
        this.oidcRestClient = oidcRestClient;
        this.fpsakBaseUrl = fpsakUrl;
    }

    @Override
    public BehandlingDto hentUtvidetBehandlingDto(String behandlingId) {
        var uri = behandlingUri(behandlingId);
        return oidcRestClient.get(uri, BehandlingDto.class);
    }

    private URI behandlingUri(String id) {
        try {
            var builder = new URIBuilder(URI.create(fpsakBaseUrl + FPSAK_BEHANDLINGER));
            builder.setParameter("behandlingId", id);
            return builder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Ikke gyldig uri for behandling", e);
        }
    }

    @Override
    public <T> Optional<T> hentFraResourceLink(ResourceLink resourceLink, Class<T> clazz) {
        var uri = URI.create(fpsakBaseUrl + resourceLink.getHref());
        return "POST".equals(resourceLink.getType().name())
                ? oidcRestClient.postReturnsOptional(uri, resourceLink.getRequestPayload(), clazz)
                : oidcRestClient.getReturnsOptional(uri, clazz);
    }

}
