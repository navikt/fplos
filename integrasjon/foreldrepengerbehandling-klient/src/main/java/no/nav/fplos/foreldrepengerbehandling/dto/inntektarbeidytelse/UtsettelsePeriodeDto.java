package no.nav.fplos.foreldrepengerbehandling.dto.inntektarbeidytelse;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.fplos.foreldrepengerbehandling.dto.kodeverk.KodeDto;

import java.time.LocalDate;

public class UtsettelsePeriodeDto {

    @JsonProperty("fom")
    private LocalDate fom;

    @JsonProperty("tom")
    private LocalDate tom;

    @JsonProperty("utsettelseArsak")
    private KodeDto utsettelseÅrsak;

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public KodeDto getUtsettelseArsak() {
        return utsettelseÅrsak;
    }
}