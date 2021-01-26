package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

import java.time.LocalDate;

public class AapneBehandlerDto {
    private BehandlingType behandlingType;
    private BehandlingVenteStatus behandlingVenteStatus;
    private LocalDate førsteVirkeMåned;
    private int antall;

    public AapneBehandlerDto(BehandlingType behandlingType, BehandlingVenteStatus påVent, LocalDate førsteVirkeMåned, int antall) {
        this.behandlingType = behandlingType;
        this.behandlingVenteStatus = påVent;
        this.førsteVirkeMåned = førsteVirkeMåned;
        this.antall = antall;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public BehandlingVenteStatus getBehandlingVenteStatus() {
        return behandlingVenteStatus;
    }

    public LocalDate getFørsteVirkeMåned() {
        return førsteVirkeMåned;
    }

    public int getAntall() {
        return antall;
    }

}
