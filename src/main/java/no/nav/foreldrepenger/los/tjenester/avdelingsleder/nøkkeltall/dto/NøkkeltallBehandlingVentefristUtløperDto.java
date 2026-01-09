package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;


public record NøkkeltallBehandlingVentefristUtløperDto(FagsakYtelseType fagsakYtelseType,
                                                       String fristUke,
                                                       Long antall) {

}
