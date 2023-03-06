package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

import java.time.LocalDate;

public record NøkkeltallBehandlingVentestatusDto(String behandlendeEnhet, BehandlingType behandlingType, BehandlingVenteStatus behandlingVenteStatus,
                                                 LocalDate førsteUttakMåned, int antall) {
}
