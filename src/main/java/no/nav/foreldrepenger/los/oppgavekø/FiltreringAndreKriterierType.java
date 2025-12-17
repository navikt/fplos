package no.nav.foreldrepenger.los.oppgavekø;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "FiltreringAndreKriterier")
@Table(name = "FILTRERING_ANDRE_KRITERIER")
public class FiltreringAndreKriterierType extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FILTRERING_ANDRE_KRIT")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_FILTRERING_ID", nullable = false)
    private OppgaveFiltrering oppgaveFiltrering;

    @Column(name = "ANDRE_KRITERIER_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private AndreKriterierType andreKriterierType;

    //Verdi som viser om filtreringen skal inkludere eller ekskludere oppgaver med det gitte innslaget.
    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "INKLUDER")
    private boolean inkluder = true;

    public FiltreringAndreKriterierType() {
        //hibernate
    }

    public FiltreringAndreKriterierType(OppgaveFiltrering oppgaveFiltrering, AndreKriterierType andreKriterierType, boolean inkluder) {
        this.oppgaveFiltrering = oppgaveFiltrering;
        this.andreKriterierType = andreKriterierType;
        this.inkluder = inkluder;
    }

    public AndreKriterierType getAndreKriterierType() {
        return andreKriterierType;
    }

    public boolean isInkluder() {
        return inkluder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltreringAndreKriterierType other)) return false;
        return this.andreKriterierType == other.andreKriterierType && this.inkluder == other.inkluder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(andreKriterierType, inkluder);
    }

}
