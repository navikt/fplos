package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.nøkkeltall.dto;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.NyeOgFerdigstilteOppgaver;

import java.time.LocalDate;

public class NyeOgFerdigstilteOppgaverDto {

    private BehandlingType behandlingType;
    private Long antallNye;
    private Long antallFerdigstilte;
    private LocalDate dato;


    public NyeOgFerdigstilteOppgaverDto(NyeOgFerdigstilteOppgaver nyeOgFerdigstilteOppgaver) {
        behandlingType = nyeOgFerdigstilteOppgaver.getBehandlingType();
        antallNye = nyeOgFerdigstilteOppgaver.getAntallNye();
        antallFerdigstilte = nyeOgFerdigstilteOppgaver.getAntallFerdigstilte();
        dato = nyeOgFerdigstilteOppgaver.getDato();
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public Long getAntallNye() {
        return antallNye;
    }

    public Long getAntallFerdigstilte() {
        return antallFerdigstilte;
    }

    public LocalDate getDato() {
        return dato;
    }
}
