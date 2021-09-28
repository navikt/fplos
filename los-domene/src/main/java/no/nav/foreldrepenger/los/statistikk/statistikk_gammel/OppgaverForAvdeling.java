package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdeling(
        FagsakYtelseType fagsakYtelseType,
        BehandlingType behandlingType,
        Boolean tilBeslutter,
        Long antall) {
}
