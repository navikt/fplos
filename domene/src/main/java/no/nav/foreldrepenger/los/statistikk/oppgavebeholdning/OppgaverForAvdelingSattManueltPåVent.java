package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

import java.time.LocalDate;

public record OppgaverForAvdelingSattManueltPåVent(FagsakYtelseType fagsakYtelseType, LocalDate behandlingFrist, Long antall) {
}
