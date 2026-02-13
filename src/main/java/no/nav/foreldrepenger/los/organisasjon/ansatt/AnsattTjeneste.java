package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AnsattTjeneste {

    private AnsattInfoKlient brukerKlient;


    AnsattTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjeneste(AnsattInfoKlient brukerKlient) {
        this.brukerKlient = brukerKlient;
    }

    public Optional<BrukerProfil> hentBrukerProfil(String ident) {
        var trimmed = ident.trim().toUpperCase();
        return brukerKlient.brukerProfil(trimmed);
    }

    public Optional<BrukerProfil> refreshBrukerProfil(String ident) {
        var trimmed = ident.trim().toUpperCase();
        return brukerKlient.refreshAnsattInfoForIdent(trimmed);
    }

}
