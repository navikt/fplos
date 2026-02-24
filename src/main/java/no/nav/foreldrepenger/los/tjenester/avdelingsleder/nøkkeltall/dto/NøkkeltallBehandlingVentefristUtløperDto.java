package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;


public record NøkkeltallBehandlingVentefristUtløperDto(@NotNull FagsakYtelseType fagsakYtelseType, @NotNull String fristUke, @NotNull Long antall) {

}
