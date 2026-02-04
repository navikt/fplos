package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.vedtak.felles.integrasjon.ansatt.AnsattInfoDto;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPTILGANG)
public class RefreshAnsattInfoKlient {

    private final RestClient restClient;
    private final RestConfig restConfig;
    private final URI ansattIdentUri;
    private final URI ansattOidUri;


    protected RefreshAnsattInfoKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.ansattIdentUri = UriBuilder.fromUri(restConfig.fpContextPath()).path("/api/ansattinfo/refresh-ansatt-ident").build();
        this.ansattOidUri = UriBuilder.fromUri(restConfig.fpContextPath()).path("/api/ansattinfo/refresh-ansatt-oid").build();
    }

    public Optional<BrukerProfil> refreshAnsattInfoForIdent(String ident) {
        var request = RestRequest.newPOSTJson(new AnsattInfoDto.IdentRequest(ident), ansattIdentUri, restConfig);
        return restClient.sendReturnOptional(request, AnsattInfoDto.Respons.class)
            .map(p -> new BrukerProfil(p.ansattOid(), p.ansattIdent(), p.navn(), p.ansattVedEnhetId()));
    }

    public Optional<BrukerProfil> refreshAnsattInfoForOid(UUID oid) {
        var request = RestRequest.newPOSTJson(new AnsattInfoDto.OidRequest(oid), ansattOidUri, restConfig);
        return restClient.sendReturnOptional(request, AnsattInfoDto.Respons.class)
            .map(p -> new BrukerProfil(p.ansattOid(), p.ansattIdent(), p.navn(), p.ansattVedEnhetId()));
    }

}
