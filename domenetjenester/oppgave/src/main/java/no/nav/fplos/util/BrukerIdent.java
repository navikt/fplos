package no.nav.fplos.util;

import no.nav.vedtak.sikkerhet.context.SubjectHandler;

import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;

public class BrukerIdent {

    public static String brukerIdent() {
        String brukerident = SubjectHandler.getSubjectHandler().getUid();
        return brukerident != null ? brukerident.toUpperCase() : BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }

}
