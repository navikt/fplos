package no.nav.foreldrepenger.los.felles.util;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

import java.util.Optional;

public class BrukerIdent {

    public static String brukerIdent() {
        return Optional.ofNullable(SubjectHandler.getSubjectHandler())
                .map(SubjectHandler::getUid)
                .map(String::toUpperCase)
                .orElse(BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES);
    }

}
