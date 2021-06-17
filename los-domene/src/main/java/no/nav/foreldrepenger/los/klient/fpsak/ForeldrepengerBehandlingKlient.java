package no.nav.foreldrepenger.los.klient.fpsak;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@Dependent
public class ForeldrepengerBehandlingKlient implements ForeldrepengerBehandling {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerBehandlingKlient.class);

    private static final String FPSAK_BEHANDLINGER = "/fpsak/api/behandlinger";
    private static final String AKSJONSPUNKTER_LINK = "aksjonspunkter";
    private static final String INNTEKT_ARBEID_YTELSE_LINK = "inntekt-arbeid-ytelse";
    private static final String UTTAK_KONTROLLER_FAKTA_PERIODER_LINK = "uttak-kontroller-fakta-perioder";
    private static final String KONTROLLRESULTAT = "kontrollresultat";
    private static final String YTELSEFORDELING_LINK = "ytelsefordeling";

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

    public Optional<Long> getFpsakInternBehandlingId(BehandlingId eksternBehandlingId) {
        var uri = behandlingUri(eksternBehandlingId.toString());
        try {
            var behandlingDto = oidcRestClient.get(uri, BehandlingDto.class);
            return Optional.ofNullable(behandlingDto.id());
        } catch (ManglerTilgangException e) {
            throw new InternIdMappingException(eksternBehandlingId);
        }
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oidcRestClient=" + oidcRestClient + ", fpsakBaseUrl=" + fpsakBaseUrl + "]";
    }

}
