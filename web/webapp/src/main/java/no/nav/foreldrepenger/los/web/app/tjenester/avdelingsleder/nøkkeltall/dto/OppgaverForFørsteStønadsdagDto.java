package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForFørsteStønadsdag;

public record OppgaverForFørsteStønadsdagDto(LocalDate forsteStonadsdag, LocalDate førsteStønadsdag, Long antall) {

    public OppgaverForFørsteStønadsdagDto(OppgaverForFørsteStønadsdag oppgaverForFørsteStønadsdag) {
        this(oppgaverForFørsteStønadsdag.førsteStonadsdag(), oppgaverForFørsteStønadsdag.førsteStonadsdag(),
                oppgaverForFørsteStønadsdag.antall());
    }
}
