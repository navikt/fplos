package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForFørsteStønadsdagUkeMåned(String behandlendeEnhet,
                                                  FagsakYtelseType fagsakYtelseType,
                                                  LocalDate førsteStønadsdag,
                                                  Long antall) {
}
