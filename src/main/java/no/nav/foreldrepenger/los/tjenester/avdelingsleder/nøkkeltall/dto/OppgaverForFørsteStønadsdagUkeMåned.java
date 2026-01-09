package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForFørsteStønadsdagUkeMåned(FagsakYtelseType fagsakYtelseType,
                                                  LocalDate førsteStønadsdag,
                                                  Long antall) {
}
