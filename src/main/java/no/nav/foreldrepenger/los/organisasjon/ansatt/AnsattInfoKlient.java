package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.Optional;
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

    public Optional<BrukerProfil> brukerProfil(String ident) {
        var profil = super.hentAnsattInfoForIdent(ident);
        return Optional.ofNullable(profil).map(p -> new BrukerProfil(p.ansattOid(), p.ansattIdent(), p.navn(), p.ansattVedEnhetId()));
    }

    public Optional<BrukerProfil> brukerProfil(UUID saksbehandler) {
        var profil = super.hentAnsattInfoForOid(saksbehandler);
        return Optional.ofNullable(profil).map(p -> new BrukerProfil(p.ansattOid(), p.ansattIdent(), p.navn(), p.ansattVedEnhetId()));
    }
}
