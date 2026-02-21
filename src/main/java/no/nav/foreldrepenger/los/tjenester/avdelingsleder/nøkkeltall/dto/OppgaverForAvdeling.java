package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdeling(@NotNull FagsakYtelseType fagsakYtelseType, @NotNull BehandlingType behandlingType, @NotNull Boolean tilBehandling,
                                  @NotNull Long antall) {
}
