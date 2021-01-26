package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

import java.time.LocalDate;

public class AapneBehandlerDto {
    private BehandlingType behandlingType;
    private BehandlingVenteStatus behandlingVenteStatus;
    private LocalDate førsteUttakMåned;
    private int antall;

    public AapneBehandlerDto(BehandlingType behandlingType, BehandlingVenteStatus påVent, LocalDate førsteUttakMåned, int antall) {
        this.behandlingType = behandlingType;
        this.behandlingVenteStatus = påVent;
        this.førsteUttakMåned = førsteUttakMåned;
        this.antall = antall;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public BehandlingVenteStatus getBehandlingVenteStatus() {
        return behandlingVenteStatus;
    }

    public LocalDate getFørsteUttakMåned() {
        return førsteUttakMåned;
    }

    public int getAntall() {
        return antall;
    }

}
