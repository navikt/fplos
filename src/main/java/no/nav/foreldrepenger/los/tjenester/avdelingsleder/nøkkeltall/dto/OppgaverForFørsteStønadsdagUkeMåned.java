package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

public record OppgaverForFørsteStønadsdagUkeMåned(LocalDate førsteStønadsdag,
                                                  String førsteStønadsdagTekst,
                                                  Long antall) {
}
