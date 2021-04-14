package no.nav.foreldrepenger.los.felles.util;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

public class BrukerIdent {

    public static String brukerIdent() {
        var brukerident = SubjectHandler.getSubjectHandler().getUid();
        return brukerident != null ? brukerident.toUpperCase() : BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }

}
