package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.foreldrepenger.konfig.KonfigVerdi;

import static no.nav.foreldrepenger.los.felles.util.OptionalUtil.tryOrEmpty;

@ApplicationScoped
public class EnhetstilgangConnection {
    private static final Logger LOG = LoggerFactory.getLogger(EnhetstilgangConnection.class);

    private static final String PATH = "/api/v1/tilgang/";
    private String host;
    private OidcRestClient httpClient;
    private boolean enabled;

    @Inject
    public EnhetstilgangConnection(@KonfigVerdi(value = "axsys.url", defaultVerdi = "http://axsys.default") String host,
                                   @KonfigVerdi(value = "axsys.enabled", defaultVerdi = "true") boolean enabled,
                                   OidcRestClient httpClient) {
        this.host = host;
        this.httpClient = httpClient;
        this.enabled = enabled;
    }

    EnhetstilgangConnection() {
        // CDI
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Optional<EnhetstilgangResponse> hentEnhetstilganger(String ident) {
        return tryOrEmpty(() -> hentEnhetstilganger(uri(ident)));
    }

    private EnhetstilgangResponse hentEnhetstilganger(URI uri) {
        return httpClient.get(uri, EnhetstilgangResponse.class);
    }

    private URI uri(String ident) {
        return URI.create(host + PATH + ident);
    }
}
