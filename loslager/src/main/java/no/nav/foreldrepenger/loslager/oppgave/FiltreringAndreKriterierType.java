package no.nav.foreldrepenger.loslager.oppgave;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "FiltreringAndreKriterier")
@Table(name = "FILTRERING_ANDRE_KRITERIER")
public class FiltreringAndreKriterierType extends BaseEntitet{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FILTRERING_ANDRE_KRIT")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_FILTRERING_ID", nullable = false)
    private OppgaveFiltrering oppgaveFiltrering;

    @Column(name = "OPPGAVE_FILTRERING_ID", updatable = false, insertable = false)
    private Long oppgaveFiltreringId;

    @Column(name = "ANDRE_KRITERIER_TYPE", updatable = false, insertable = false)
    private String andreKriterier;

    @Column(name = "ANDRE_KRITERIER_TYPE", nullable = false)
    @Convert(converter = AndreKriterierType.KodeverdiConverter.class)
    private AndreKriterierType andreKriterierType;

    //Verdi som viser om filtreringen skal inkludere eller ekskludere oppgaver med det gitte innslaget.
    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "INKLUDER")
    private boolean inkluder = true;

    public FiltreringAndreKriterierType(){
        //CDI
    }

    public FiltreringAndreKriterierType(OppgaveFiltrering oppgaveFiltrering, AndreKriterierType andreKriterierType, boolean inkluder){
        this.oppgaveFiltrering = oppgaveFiltrering;
        this.andreKriterierType = andreKriterierType;
        this.inkluder = inkluder;
    }

    public OppgaveFiltrering getOppgaveFiltrering() {
        return oppgaveFiltrering;
    }

    public AndreKriterierType getAndreKriterierType() {
        return andreKriterierType;
    }

    public boolean isInkluder() {
        return inkluder;
    }

    public boolean isEkskluder() {
        return !inkluder;
    }
}
