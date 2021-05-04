package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForAvdelingPerDato;

public record OppgaverForAvdelingPerDatoDto(FagsakYtelseType fagsakYtelseType,
                                            BehandlingType behandlingType,
                                            LocalDate opprettetDato,
                                            Long antall) {

    public OppgaverForAvdelingPerDatoDto(OppgaverForAvdelingPerDato oppgaverForAvdeling) {
        this(oppgaverForAvdeling.getFagsakYtelseType(), oppgaverForAvdeling.getBehandlingType(), oppgaverForAvdeling.getOpprettetDato(),
                oppgaverForAvdeling.getAntall());
    }
}
