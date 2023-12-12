package no.nav.foreldrepenger.los.oppgavekø;

import java.util.Objects;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

public record OppgaveFiltreringKnytning(Long oppgaveId, Long oppgaveFiltreringId, BehandlingType behandlingType) {
    public OppgaveFiltreringKnytning {
        Objects.requireNonNull(oppgaveId, "oppgaveId");
        Objects.requireNonNull(oppgaveFiltreringId, "oppgaveFiltreringId");
        Objects.requireNonNull(behandlingType, "behandlingType");
    }
}
