package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;


public record NøkkeltallBehandlingVentefristUtløperDto(String behandlendeEnhet, FagsakYtelseType fagsakYtelseType, LocalDate behandlingFrist, Long antall) {

}
