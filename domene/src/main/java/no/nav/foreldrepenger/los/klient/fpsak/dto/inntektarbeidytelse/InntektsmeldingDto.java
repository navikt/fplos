package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDate;

public record InntektsmeldingDto(String arbeidsgiver,
                                 String arbeidsgiverOrgnr,
                                 LocalDate arbeidsgiverStartdato,
                                 Beløp getRefusjonBeløpPerMnd) {

    @Override
    public String toString() {
        return "InntektsmeldingDto{" + "arbeidsgiverOrgnr='" + maskOrgnr(arbeidsgiverOrgnr) + '\'' + ", arbeidsgiverStartdato="
                + arbeidsgiverStartdato + ", getRefusjonBeløpPerMnd=" + getRefusjonBeløpPerMnd + '}';
    }

    private static String maskOrgnr(String arbeidsgiverOrgnr) {
        return arbeidsgiverOrgnr.replaceAll("^\\d{5}", "*****");
    }
}
