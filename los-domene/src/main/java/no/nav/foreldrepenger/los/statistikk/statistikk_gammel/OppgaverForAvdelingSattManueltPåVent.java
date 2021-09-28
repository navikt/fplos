package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdelingSattManueltPåVent(
        FagsakYtelseType fagsakYtelseType,
        LocalDate estimertFrist,
        Long antall) {
}
