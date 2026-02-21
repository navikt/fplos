package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;

public record NøkkeltallBehandlingFørsteUttakDto(@NotNull BehandlingType behandlingType, @NotNull BehandlingVenteStatus behandlingVenteStatus,
                                                 LocalDate førsteUttakMåned, @NotNull int antall) {
}
