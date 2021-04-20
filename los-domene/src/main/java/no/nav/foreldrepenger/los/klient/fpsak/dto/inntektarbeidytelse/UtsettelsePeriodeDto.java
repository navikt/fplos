package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.klient.fpsak.dto.kodeverk.KodeDto;

public record UtsettelsePeriodeDto(LocalDate fom, LocalDate tom, KodeDto utsettelseArsak) {


}
