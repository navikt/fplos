package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.vedtak.felles.integrasjon.ansatt.AbstractAnsattInfoKlient;
import no.nav.vedtak.felles.integrasjon.ansatt.AnsattInfoDto;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPTILGANG)
public class AnsattInfoKlient extends AbstractAnsattInfoKlient {

    private final RestClient restClient;
    private final RestConfig restConfig;
    private final URI refreshAnsattIdentUri;

    public AnsattInfoKlient() {
        super();
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
        this.refreshAnsattIdentUri = UriBuilder.fromUri(restConfig.fpContextPath()).path("/api/ansattinfo/refresh-ansatt-ident").build();
    }

    public Optional<BrukerProfil> brukerProfil(String ident) {
        var profil = super.hentAnsattInfoForIdent(ident);
        return Optional.ofNullable(profil).map(p -> new BrukerProfil(p.ansattOid(), p.ansattIdent(), p.navn(), p.ansattVedEnhetId()));
    }

    public Optional<BrukerProfil> brukerProfil(UUID saksbehandler) {
        var profil = super.hentAnsattInfoForOid(saksbehandler);
        return Optional.ofNullable(profil).map(p -> new BrukerProfil(p.ansattOid(), p.ansattIdent(), p.navn(), p.ansattVedEnhetId()));
    }

    public Optional<BrukerProfil> refreshAnsattInfoForIdent(String ident) {
        var request = RestRequest.newPOSTJson(new AnsattInfoDto.IdentRequest(ident), refreshAnsattIdentUri, restConfig);
        return restClient.sendReturnOptional(request, AnsattInfoDto.Respons.class)
            .map(p -> new BrukerProfil(p.ansattOid(), p.ansattIdent(), p.navn(), p.ansattVedEnhetId()));
    }
}
