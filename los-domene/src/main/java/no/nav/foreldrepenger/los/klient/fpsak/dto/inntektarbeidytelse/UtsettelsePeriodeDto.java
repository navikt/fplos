package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.los.klient.fpsak.dto.kodeverk.KodeDto;

public record UtsettelsePeriodeDto(LocalDate fom, LocalDate tom, @JsonProperty("utsettelseArsak") KodeDto uttsettelseÅrsak) {

    
}
