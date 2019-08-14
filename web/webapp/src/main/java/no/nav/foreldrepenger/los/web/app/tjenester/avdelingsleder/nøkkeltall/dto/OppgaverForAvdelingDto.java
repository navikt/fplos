package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.statistikk.OppgaverForAvdeling;

public class OppgaverForAvdelingDto {

    private FagsakYtelseType fagsakYtelseType;
    private BehandlingType behandlingType;
    private Boolean tilBehandling;
    private Long antall;


    public OppgaverForAvdelingDto(OppgaverForAvdeling oppgaverForAvdeling) {
        fagsakYtelseType = oppgaverForAvdeling.getFagsakYtelseType();
        behandlingType = oppgaverForAvdeling.getBehandlingType();
        tilBehandling = !oppgaverForAvdeling.getTilBeslutter();
        antall = oppgaverForAvdeling.getAntall();
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public Boolean getTilBehandling() {
        return tilBehandling;
    }

    public Long getAntall() {
        return antall;
    }
}
