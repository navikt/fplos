package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForAvdeling;

public record OppgaverForAvdelingDto(FagsakYtelseType fagsakYtelseType,
                                     BehandlingType behandlingType,
                                     Boolean tilBehandling,
                                     Long antall) {

    public OppgaverForAvdelingDto(OppgaverForAvdeling oppgaverForAvdeling) {
        this(oppgaverForAvdeling.getFagsakYtelseType(), oppgaverForAvdeling.getBehandlingType(),
                !oppgaverForAvdeling.getTilBeslutter(), oppgaverForAvdeling.getAntall());
    }
}
