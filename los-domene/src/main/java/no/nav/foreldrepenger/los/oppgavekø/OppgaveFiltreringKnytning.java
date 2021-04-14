package no.nav.foreldrepenger.los.oppgavekø;

import java.util.Objects;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

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
    public String toString() {
        return "OppgaveFiltreringKnytning{" + "oppgaveId=" + oppgaveId + ", oppgaveFiltreringId=" + oppgaveFiltreringId
                + ", behandlingType=" + behandlingType + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (OppgaveFiltreringKnytning) o;
        return oppgaveId.equals(that.oppgaveId) && oppgaveFiltreringId.equals(that.oppgaveFiltreringId) && behandlingType == that.behandlingType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oppgaveId, oppgaveFiltreringId, behandlingType);
    }
}
