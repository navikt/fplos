package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

import java.time.LocalDate;

public record OppgaverForAvdelingPerDato(
        FagsakYtelseType fagsakYtelseType,
        BehandlingType behandlingType,
        LocalDate opprettetDato,
        Long antall) {
        }
