package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

public record NøkkeltallBehandlingFørsteUttakDto(BehandlingType behandlingType, BehandlingVenteStatus behandlingVenteStatus,
                                                 LocalDate førsteUttakMåned, int antall) {
}
