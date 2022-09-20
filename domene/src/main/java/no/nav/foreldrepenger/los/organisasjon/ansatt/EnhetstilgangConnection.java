package no.nav.foreldrepenger.los.organisasjon.ansatt;

import static no.nav.foreldrepenger.los.felles.util.OptionalUtil.tryOrEmpty;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.CONTEXT, endpointProperty = "axsys.url", endpointDefault = "http://axsys.default")
public class EnhetstilgangConnection {
    private static final Logger LOG = LoggerFactory.getLogger(EnhetstilgangConnection.class);

    private static final String PATH = "/api/v1/tilgang/";
    private String host;
    private RestClient httpClient;
    private boolean enabled;

    @Inject
    public EnhetstilgangConnection(RestClient httpClient,
                                   @KonfigVerdi(value = "axsys.enabled", defaultVerdi = "true") boolean enabled) {
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
        var request = RestRequest.newGET(uri, TokenFlow.CONTEXT, null);
        return httpClient.send(request, EnhetstilgangResponse.class);
    }

    private URI uri(String ident) {
        return URI.create(host + PATH + ident);
    }
}
