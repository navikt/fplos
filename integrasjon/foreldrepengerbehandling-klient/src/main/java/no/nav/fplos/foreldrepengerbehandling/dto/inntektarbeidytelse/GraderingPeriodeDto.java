package no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GraderingPeriodeDto {
    @JsonProperty("fom")
    private LocalDate fom;

    @JsonProperty("tom")
    private LocalDate tom;

    @JsonProperty("arbeidsprosent")
    private BigDecimal arbeidsprosent;

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public BigDecimal getArbeidsprosent() {
        return arbeidsprosent;
    }
}