package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto;

import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForFørsteStønadsdag;

import java.time.LocalDate;

public class OppgaverForFørsteStønadsdagDto {

    private LocalDate forsteStonadsdag;
    private Long antall;

    public OppgaverForFørsteStønadsdagDto(OppgaverForFørsteStønadsdag oppgaverForFørsteStønadsdag) {
        this.forsteStonadsdag = oppgaverForFørsteStønadsdag.getForsteStonadsdag();
        this.antall = oppgaverForFørsteStønadsdag.getAntall();
    }

    public LocalDate getForsteStonadsdag() {
        return forsteStonadsdag;
    }

    public Long getAntall() {
        return antall;
    }
}
