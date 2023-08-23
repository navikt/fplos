package no.nav.foreldrepenger.los.organisasjon.ansatt;

import static no.nav.foreldrepenger.los.felles.util.OptionalUtil.tryOrEmpty;

import java.net.URI;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, endpointProperty = "axsys.url", endpointDefault = "http://axsys.org",
    scopesProperty = "axsys.scopes", scopesDefault = "api://prod-fss.org.axsys/.default")
public class EnhetstilgangConnection {

    private static final String PATH = "/api/v2/tilgang/";

    private final RestClient httpClient;
    private final RestConfig restConfig;
    private final boolean enabled;

    @Inject
    public EnhetstilgangConnection(@KonfigVerdi(value = "axsys.enabled", defaultVerdi = "true") boolean enabled) {
        this.httpClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Optional<EnhetstilgangResponse> hentEnhetstilganger(String ident) {
        return tryOrEmpty(() -> hentEnhetstilganger(uri(ident)), "axsys");
    }

    private EnhetstilgangResponse hentEnhetstilganger(URI uri) {
        var request = RestRequest.newGET(uri, restConfig);
        return httpClient.send(request, EnhetstilgangResponse.class);
    }

    private URI uri(String ident) {
        return URI.create(restConfig.endpoint() + PATH + ident);
    }
}
