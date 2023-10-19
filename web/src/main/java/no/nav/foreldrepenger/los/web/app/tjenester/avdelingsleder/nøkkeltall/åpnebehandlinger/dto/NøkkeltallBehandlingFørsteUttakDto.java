package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

public record NøkkeltallBehandlingFørsteUttakDto(String behandlendeEnhet, BehandlingType behandlingType, BehandlingVenteStatus behandlingVenteStatus,
                                                 LocalDate førsteUttakMåned, int antall) {
}
