package no.nav.foreldrepenger.los.oppgavekø;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;


public class OppgaveFiltreringKnytning {
    private final Long oppgaveId;
    private final Long oppgaveFiltreringId;
    private final BehandlingType behandlingType;

    public OppgaveFiltreringKnytning(Long oppgaveId, Long oppgaveFiltreringId, BehandlingType behandlingType) {
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
}
