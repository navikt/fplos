package no.nav.foreldrepenger.los.tjenester.felles.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class OppgaveDtoMedStatus extends OppgaveDto {
    @NotNull private final OppgaveBehandlingStatus oppgaveBehandlingStatus;

    public OppgaveDtoMedStatus(OppgaveDto oppgaveDto, OppgaveBehandlingStatus status) {
        super(oppgaveDto);
        this.oppgaveBehandlingStatus = status;
    }

    public OppgaveBehandlingStatus getOppgaveBehandlingStatus() {
        return oppgaveBehandlingStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OppgaveDtoMedStatus that))
            return false;
        if (!super.equals(o))
            return false;
        return Objects.equals(oppgaveBehandlingStatus, that.oppgaveBehandlingStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), oppgaveBehandlingStatus);
    }
}
