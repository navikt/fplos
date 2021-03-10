package no.nav.foreldrepenger.los.oppgavekø;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

import java.util.Objects;

public class OppgaveFiltreringKnytning {
    private final Long oppgaveId;
    private final Long oppgaveFiltreringId;
    private final BehandlingType behandlingType;

    public OppgaveFiltreringKnytning(Long oppgaveId, Long oppgaveFiltreringId, BehandlingType behandlingType) {
        Objects.requireNonNull(oppgaveId, "oppgaveId");
        Objects.requireNonNull(oppgaveFiltreringId, "oppgaveFiltreringId");
        Objects.requireNonNull(behandlingType, "behandlingType");
        this.oppgaveId = oppgaveId;
        this.oppgaveFiltreringId = oppgaveFiltreringId;
        this.behandlingType = behandlingType;
    }

    public Long getOppgaveId() {
        return oppgaveId;
    }

    public Long getOppgaveFiltreringId() {
        return oppgaveFiltreringId;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OppgaveFiltreringKnytning that = (OppgaveFiltreringKnytning) o;
        return oppgaveId.equals(that.oppgaveId) && oppgaveFiltreringId.equals(that.oppgaveFiltreringId) && behandlingType == that.behandlingType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oppgaveId, oppgaveFiltreringId, behandlingType);
    }
}
