package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdelingPerDato(@NotNull FagsakYtelseType fagsakYtelseType, @NotNull BehandlingType behandlingType,
                                         @NotNull LocalDate opprettetDato, @NotNull LocalDate statistikkDato, @NotNull Long antall) {
}
