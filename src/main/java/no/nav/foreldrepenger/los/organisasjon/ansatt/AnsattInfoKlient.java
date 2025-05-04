package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.UUID;

import jakarta.enterprise.context.Dependent;
import no.nav.vedtak.felles.integrasjon.ansatt.AbstractAnsattInfoKlient;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPTILGANG)
public class AnsattInfoKlient extends AbstractAnsattInfoKlient {

    public AnsattInfoKlient() {
        super();
    }

    public BrukerProfil brukerProfil(String ident) {
        var profil = super.hentAnsattInfoForIdent(ident);
        return profil != null ? new BrukerProfil(profil.ansattOid(), profil.ansattIdent(), profil.navn(), profil.ansattVedEnhetId()) : null;
    }

    public BrukerProfil brukerProfil(UUID saksbehandler) {
        var profil = super.hentAnsattInfoForOid(saksbehandler);
        return profil != null ? new BrukerProfil(profil.ansattOid(), profil.ansattIdent(), profil.navn(), profil.ansattVedEnhetId()) : null;
    }
}
