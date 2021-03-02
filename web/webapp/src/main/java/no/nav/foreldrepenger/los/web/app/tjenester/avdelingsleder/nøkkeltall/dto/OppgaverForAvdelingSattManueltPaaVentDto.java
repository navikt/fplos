package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto;

import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.domenetjenester.statistikk_gammel.OppgaverForAvdelingSattManueltPaaVent;

import java.time.LocalDate;

public class OppgaverForAvdelingSattManueltPaaVentDto {
    private FagsakYtelseType fagsakYtelseType;
    private LocalDate behandlingFrist;
    private Long antall;


    public OppgaverForAvdelingSattManueltPaaVentDto(OppgaverForAvdelingSattManueltPaaVent oppgaverForAvdeling) {
        fagsakYtelseType = oppgaverForAvdeling.getFagsakYtelseType();
        behandlingFrist = oppgaverForAvdeling.getEstimertFrist();
        antall = oppgaverForAvdeling.getAntall();
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public LocalDate getBehandlingFrist() {
        return behandlingFrist;
    }

    public Long getAntall() {
        return antall;
    }
}
