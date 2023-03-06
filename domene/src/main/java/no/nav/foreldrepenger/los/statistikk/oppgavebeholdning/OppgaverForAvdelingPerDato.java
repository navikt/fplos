package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdelingPerDato(FagsakYtelseType fagsakYtelseType, BehandlingType behandlingType, LocalDate opprettetDato, Long antall) {
}
