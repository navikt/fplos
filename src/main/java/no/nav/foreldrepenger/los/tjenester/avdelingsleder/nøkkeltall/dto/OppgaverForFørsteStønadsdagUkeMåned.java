package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForFørsteStønadsdagUkeMåned(@NotNull FagsakYtelseType fagsakYtelseType,
                                                  @NotNull LocalDate førsteStønadsdag,
                                                  @NotNull Long antall) {
}
