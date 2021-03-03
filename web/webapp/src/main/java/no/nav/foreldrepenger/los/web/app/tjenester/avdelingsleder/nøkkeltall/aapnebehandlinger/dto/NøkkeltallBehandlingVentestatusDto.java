package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;

import java.time.LocalDate;

public class NøkkeltallBehandlingVentestatusDto {
    private BehandlingType behandlingType;
    private BehandlingVenteStatus behandlingVenteStatus;
    private LocalDate førsteUttakMåned;
    private int antall;
    private String behandlendeEnhet;

    @JsonCreator
    public NøkkeltallBehandlingVentestatusDto(@JsonProperty("behandlendeEnhet") String behandlendeEnhet,
                                              @JsonProperty("behandlingType") BehandlingType behandlingType,
                                              @JsonProperty("behandlingVenteStatus") BehandlingVenteStatus behandlingVenteStatus,
                                              @JsonProperty("førsteUttakMåned") LocalDate førsteUttakMåned,
                                              @JsonProperty("antall") int antall) {
        this.behandlendeEnhet = behandlendeEnhet;
        this.behandlingType = behandlingType;
        this.behandlingVenteStatus = behandlingVenteStatus;
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

    @JsonIgnore
    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }
}
