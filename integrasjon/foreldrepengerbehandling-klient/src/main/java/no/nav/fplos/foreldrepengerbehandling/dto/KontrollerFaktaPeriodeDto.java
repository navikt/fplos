package no.nav.fplos.foreldrepengerbehandling.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KontrollerFaktaPeriodeDto {

    private BigDecimal arbeidstidsprosent;

    public void setArbeidstidsprosent(BigDecimal arbeidstidsprosent) {
        this.arbeidstidsprosent = arbeidstidsprosent;
    }

    public BigDecimal getArbeidstidsprosent() {
        return arbeidstidsprosent;
    }

}

