package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdelingSattManueltPåVent(
        FagsakYtelseType fagsakYtelseType,
        LocalDate behandlingFrist,
        Long antall) {
}
