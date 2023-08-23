package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;


public class DriftsmeldingOpprettelseDto implements AbacDto {
    @NotNull
    @Size(min = 10, max = 1500)
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    @JsonProperty(value = "melding", required = true)
    private String melding;
    @JsonProperty("aktivFra")
    private LocalDateTime aktivFra;
    @JsonProperty("aktivTil")
    private LocalDateTime aktivTil;

    public DriftsmeldingOpprettelseDto() {
        // Jackson
    }

    public void setMelding(String melding) {
        this.melding = melding;
    }

    public void setAktivFra(LocalDateTime aktivFra) {
        this.aktivFra = aktivFra;
    }

    public void setAktivTil(LocalDateTime aktivTil) {
        this.aktivTil = aktivTil;
    }

    public String getMelding() {
        return melding;
    }

    public LocalDateTime getAktivFra() {
        return aktivFra;
    }

    public LocalDateTime getAktivTil() {
        return aktivTil;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }

}
