package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.Objects;

public class OppgaveDtoMedStatus extends OppgaveDto {
    private final OppgaveBehandlingsstatus behandlingsstatus;

    public OppgaveDtoMedStatus(OppgaveDto oppgaveDto, OppgaveBehandlingsstatus status) {
        super(oppgaveDto);
        this.behandlingsstatus = status;
    }

    public OppgaveBehandlingsstatus getBehandlingsstatus() {
        return behandlingsstatus;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OppgaveDtoMedStatus that))
            return false;
        if (!super.equals(o))
            return false;
        return Objects.equals(behandlingsstatus, that.behandlingsstatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), behandlingsstatus);
    }
}
