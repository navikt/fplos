package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDate;
import java.util.List;

public record InntektsmeldingDto(String arbeidsgiver,
                                 String arbeidsgiverOrgnr,
                                 LocalDate arbeidsgiverStartdato,
                                 List<UtsettelsePeriodeDto> utsettelsePerioder,
                                 List<GraderingPeriodeDto> graderingPerioder,
                                 Beløp getRefusjonBeløpPerMnd) {

    public List<UtsettelsePeriodeDto> utsettelsePerioder() {
        return utsettelsePerioder == null ? List.of() : utsettelsePerioder;
    }

    public List<GraderingPeriodeDto> graderingPerioder() {
        return graderingPerioder == null ? List.of() : graderingPerioder;
    }

    @Override
    public String toString() {
        return "InntektsmeldingDto{" + "arbeidsgiverOrgnr='" + maskOrgnr(arbeidsgiverOrgnr) + '\'' + ", arbeidsgiverStartdato="
                + arbeidsgiverStartdato + ", utsettelsePerioder=" + utsettelsePerioder + ", graderingPerioder="
                + graderingPerioder + ", getRefusjonBeløpPerMnd=" + getRefusjonBeløpPerMnd + '}';
    }

    private static String maskOrgnr(String arbeidsgiverOrgnr) {
        return arbeidsgiverOrgnr.replaceAll("^\\d{5}", "*****");
    }
}
