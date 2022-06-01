package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

import java.time.LocalDate;

public record OppgaverForAvdelingPerDato(
        FagsakYtelseType fagsakYtelseType,
        BehandlingType behandlingType,
        LocalDate opprettetDato,
        Long antall) {
        }
