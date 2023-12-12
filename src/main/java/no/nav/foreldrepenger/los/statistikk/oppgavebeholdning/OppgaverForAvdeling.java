package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdeling(FagsakYtelseType fagsakYtelseType, BehandlingType behandlingType, Boolean tilBehandling, Long antall) {
}
