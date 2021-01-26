package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

public class AapneBehandlerDto {
    private BehandlingType behandlingType;
    private boolean erBehandlingPåVent;
    private String tidligsteFom;
    private int antall;

    public AapneBehandlerDto(BehandlingType behandlingType, boolean påVent, String tidligsteFom, int antall) {
        this.behandlingType = behandlingType;
        this.erBehandlingPåVent = påVent;
        this.tidligsteFom = tidligsteFom;
        this.antall = antall;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public boolean erBehandlingPåVent() {
        return erBehandlingPåVent;
    }

    public String getTidligsteFom() {
        return tidligsteFom;
    }

    public int getAntall() {
        return antall;
    }
}
