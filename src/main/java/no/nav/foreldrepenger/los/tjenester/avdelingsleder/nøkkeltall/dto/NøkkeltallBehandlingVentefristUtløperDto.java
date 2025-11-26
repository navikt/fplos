package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;


public record NøkkeltallBehandlingVentefristUtløperDto(String behandlendeEnhet, FagsakYtelseType fagsakYtelseType,
                                                       LocalDate behandlingFrist, String fristUke, Long antall) {

}
