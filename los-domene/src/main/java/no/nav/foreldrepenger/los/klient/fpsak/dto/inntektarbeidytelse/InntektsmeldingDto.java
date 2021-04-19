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

    public String arbeidsgiver() {
        return arbeidsgiver;
    }

    public String arbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public void setArbeidsgiver(String arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
    }

    public void setArbeidsgiverOrgnr(String arbeidsgiverOrgnr) {
        this.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
    }

    public LocalDate arbeidsgiverStartdato() {
        return arbeidsgiverStartdato;
    }

    public void arbeidsgiverStartdato(LocalDate arbeidsgiverStartdato) {
        this.arbeidsgiverStartdato = arbeidsgiverStartdato;
    }

    public List<UtsettelsePeriodeDto> utsettelsePerioder() {
        return utsettelsePerioder;
    }

    public List<GraderingPeriodeDto> graderingPerioder() {
        return graderingPerioder;
    }

    public Beløp getRefusjonBeløpPerMnd() {
        return getRefusjonBeløpPerMnd;
    }

    public void setGetRefusjonBeløpPerMnd(Beløp getRefusjonBeløpPerMnd) {
        this.getRefusjonBeløpPerMnd = getRefusjonBeløpPerMnd;
    }
}
