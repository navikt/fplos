package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForAvdelingSattManueltPåVent;

public record OppgaverForAvdelingSattManueltPåVentDto(FagsakYtelseType fagsakYtelseType,
                                                      LocalDate behandlingFrist,
                                                      Long antall) {

    public OppgaverForAvdelingSattManueltPåVentDto(OppgaverForAvdelingSattManueltPåVent oppgaverForAvdeling) {
        this(oppgaverForAvdeling.getFagsakYtelseType(), oppgaverForAvdeling.getEstimertFrist(),
                oppgaverForAvdeling.getAntall());
    }
}
