package no.nav.foreldrepenger.los.statistikk;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaveEnhetYtelseBehandling(String enhet,
                                           FagsakYtelseType fagsakYtelseType,
                                           BehandlingType behandlingType,
                                           Long antall) {
}
