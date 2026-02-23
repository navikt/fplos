package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdelingPerDato(FagsakYtelseType fagsakYtelseType, BehandlingType behandlingType,
                                         LocalDate statistikkDato, Long antall, Integer opprettet, Integer avsluttet) {
}
