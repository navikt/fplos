package no.nav.foreldrepenger.los.oppgavekø;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;

@Entity(name = "FiltreringBehandlingType")
@Table(name = "FILTRERING_BEHANDLING_TYPE")
public class FiltreringBehandlingType extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FILTR_BEHANDLING_TYPE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_FILTRERING_ID", nullable = false)
    private OppgaveFiltrering oppgaveFiltrering;

    @Enumerated(EnumType.STRING)
    @Column(name = "BEHANDLING_TYPE")
    private BehandlingType behandlingType;

    public FiltreringBehandlingType() {
        //CDI
    }

    public FiltreringBehandlingType(OppgaveFiltrering oppgaveFiltrering, BehandlingType behandlingType) {
        this.oppgaveFiltrering = oppgaveFiltrering;
        this.behandlingType = behandlingType;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltreringBehandlingType other)) return false;
        return this.behandlingType == other.behandlingType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingType);
    }

}
