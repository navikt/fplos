package no.nav.foreldrepenger.los.organisasjon.ansatt;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.util.Optional;

@ApplicationScoped
public class EnhetstilgangConnection {
    private static final Logger log = LoggerFactory.getLogger(EnhetstilgangConnection.class);

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

    public EnhetstilgangConnection() {
        // CDI
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Optional<EnhetstilgangResponse> hentEnhetstilganger(String ident) {
        return hentEnhetstilganger(uri(ident));
    }

    private Optional<EnhetstilgangResponse> hentEnhetstilganger(URI uri) {
        try {
            return httpClient.getReturnsOptional(uri, EnhetstilgangResponse.class);
        } catch (IntegrasjonException e) {
            // best effort for å stoppe unødvendige 404
            if (e.getFeil().getFeilmelding().contains("http-kode '404'")) {
                log.info("Finner ikke ident i Axsys, returnerer tomt resultat", e);
                return Optional.empty();
            } else throw e;
        }
    }

    private URI uri(String ident) {
        return URI.create(host + PATH + ident);
    }
}
