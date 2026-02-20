package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaveEndringForAvdelingPerDato(FagsakYtelseType fagsakYtelseType, BehandlingType behandlingType,
                                               LocalDate statistikkDato, Integer opprettet, Integer avsluttet) {
}
