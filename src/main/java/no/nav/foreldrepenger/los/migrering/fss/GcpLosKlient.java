package no.nav.foreldrepenger.los.migrering.fss;

import java.net.URI;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.foreldrepenger.los.migrering.dto.BulkDataWrapper;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;


@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPLOSGCP)
public class GcpLosKlient {

    private final RestClient klient;
    private final RestConfig restConfig;
    private final URI baseUri;

    public GcpLosKlient() {
        this.klient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.baseUri = restConfig.endpoint();
    }

    public void lagreBulkData(BulkDataWrapper bulkData) {
        var target = UriBuilder.fromUri(baseUri)
            .path("/api/gcp-migrering/lagre-bulk")
            .build();

        klient.send(RestRequest.newPOSTJson(bulkData, target, restConfig), String.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }
}
