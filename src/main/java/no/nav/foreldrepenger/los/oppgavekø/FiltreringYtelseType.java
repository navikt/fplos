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
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

@Entity(name = "FiltreringYtelseType")
@Table(name = "FILTRERING_YTELSE_TYPE")
public class FiltreringYtelseType extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FILTRERING_YTELSE_TYPE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_FILTRERING_ID", nullable = false)
    private OppgaveFiltrering oppgaveFiltrering;

    @Enumerated(EnumType.STRING)
    @Column(name = "FAGSAK_YTELSE_TYPE")
    private FagsakYtelseType fagsakYtelseType;

    public FiltreringYtelseType() {
        //Hibernate
    }

    public FiltreringYtelseType(OppgaveFiltrering oppgaveFiltrering, FagsakYtelseType fagsakYtelseTypeKode) {
        this.oppgaveFiltrering = oppgaveFiltrering;
        this.fagsakYtelseType = fagsakYtelseTypeKode;
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltreringYtelseType other)) return false;
        return this.fagsakYtelseType == other.fagsakYtelseType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fagsakYtelseType);
    }

}
