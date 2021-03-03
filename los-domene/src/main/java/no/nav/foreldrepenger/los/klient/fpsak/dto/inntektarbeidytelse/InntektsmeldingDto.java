package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InntektsmeldingDto {
    private String arbeidsgiver;
    private String arbeidsgiverOrgnr;
    private LocalDate arbeidsgiverStartdato;

    private List<UtsettelsePeriodeDto> utsettelsePerioder = new ArrayList<>();
    private List<GraderingPeriodeDto> graderingPerioder = new ArrayList<>();
    private Beløp getRefusjonBeløpPerMnd;

    InntektsmeldingDto() {
        // trengs for deserialisering av JSON
    }

    public String getArbeidsgiver() {
        return arbeidsgiver;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public void setArbeidsgiver(String arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
    }

    public void setArbeidsgiverOrgnr(String arbeidsgiverOrgnr) {
        this.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
    }

    public LocalDate getArbeidsgiverStartdato() {
        return arbeidsgiverStartdato;
    }

    public void setArbeidsgiverStartdato(LocalDate arbeidsgiverStartdato) {
        this.arbeidsgiverStartdato = arbeidsgiverStartdato;
    }

    public List<UtsettelsePeriodeDto> getUtsettelsePerioder() {
        return utsettelsePerioder;
    }

    public List<GraderingPeriodeDto> getGraderingPerioder() {
        return graderingPerioder;
    }

    public Beløp getGetRefusjonBeløpPerMnd() {
        return getRefusjonBeløpPerMnd;
    }

    public void setGetRefusjonBeløpPerMnd(Beløp getRefusjonBeløpPerMnd) {
        this.getRefusjonBeløpPerMnd = getRefusjonBeløpPerMnd;
    }
}
