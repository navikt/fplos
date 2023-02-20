package no.nav.foreldrepenger.los.felles.util;

import java.util.Optional;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.sikkerhet.kontekst.Kontekst;
import no.nav.vedtak.sikkerhet.kontekst.KontekstHolder;

public class BrukerIdent {

    private BrukerIdent() {
    }

    public static String brukerIdent() {
        return Optional.ofNullable(KontekstHolder.getKontekst())
                .map(Kontekst::getUid)
                .map(String::toUpperCase)
                .orElse(BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES);
    }

}
