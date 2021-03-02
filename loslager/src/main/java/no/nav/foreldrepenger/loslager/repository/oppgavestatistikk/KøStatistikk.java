package no.nav.foreldrepenger.loslager.repository.oppgavestatistikk;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

import java.time.LocalDate;
import java.util.Objects;

public class KøStatistikk {
    private final KøOppgaveHendelse køOppgaveHendelse;
    private final BehandlingType behandlingType;
    private final LocalDate dato;
    private final Long antall;

    public KøStatistikk(LocalDate dato, BehandlingType behandlingType, KøOppgaveHendelse køOppgaveHendelse, Long antall) {
        this.køOppgaveHendelse = køOppgaveHendelse;
        this.behandlingType = behandlingType;
        this.dato = dato;
        this.antall = antall;
    }

    public LocalDate getDato() {
        return dato;
    }

    public Long getAntall() {
        return antall;
    }

    public KøOppgaveHendelse getHendelse() {
        return køOppgaveHendelse;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KøStatistikk that = (KøStatistikk) o;
        return køOppgaveHendelse == that.køOppgaveHendelse && behandlingType == that.behandlingType && Objects.equals(dato, that.dato) && Objects.equals(antall, that.antall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(køOppgaveHendelse, behandlingType, dato, antall);
    }

    @Override
    public String toString() {
        return "KøStatistikk{" +
                "hendelse=" + køOppgaveHendelse +
                ", behandlingType=" + behandlingType +
                ", dato=" + dato +
                ", antall=" + antall +
                '}';
    }
}
