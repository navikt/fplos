package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.statistikk.OppgaverForAvdelingPerDato;

public class OppgaverForAvdelingPerDatoDto {

    private FagsakYtelseType fagsakYtelseType;
    private BehandlingType behandlingType;
    private java.time.LocalDate opprettetDato;
    private Long antall;


    public OppgaverForAvdelingPerDatoDto(OppgaverForAvdelingPerDato oppgaverForAvdeling) {
        fagsakYtelseType = oppgaverForAvdeling.getFagsakYtelseType();
        behandlingType = oppgaverForAvdeling.getBehandlingType();
        opprettetDato = oppgaverForAvdeling.getOpprettetDato();
        antall = oppgaverForAvdeling.getAntall();
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public LocalDate getOpprettetDato() {
        return opprettetDato;
    }

    public Long getAntall() {
        return antall;
    }
}
