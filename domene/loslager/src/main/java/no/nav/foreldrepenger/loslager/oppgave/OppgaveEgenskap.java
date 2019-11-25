package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.loslager.BaseEntitet;

@Entity(name = "OppgaveEgenskap")
@Table(name = "OPPGAVE_EGENSKAP")
public class OppgaveEgenskap extends BaseEntitet{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPPGAVE_EGENSKAP")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_ID", nullable = false)
    private Oppgave oppgave;

    @Column(name = "OPPGAVE_ID", updatable = false, insertable = false)
    private Long oppgaveId;

    @Convert(converter = AndreKriterierType.KodeverdiConverter.class)
    @Column(name = "ANDRE_KRITERIER_TYPE", updatable = false, insertable = false)
    private AndreKriterierType andreKriterierType;

    @Column(name = "SISTE_SAKSBEHANDLER_FOR_TOTR")
    private String sisteSaksbehandlerForTotrinn;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "AKTIV")
    private Boolean aktiv = Boolean.TRUE;

    public OppgaveEgenskap(){
        //CDI
    }

    public OppgaveEgenskap(Oppgave oppgave, AndreKriterierType andreKriterierType) {
        this.oppgave = oppgave;
        this.andreKriterierType = andreKriterierType;
    }

    public OppgaveEgenskap(Oppgave oppgave, AndreKriterierType andreKriterierType, String sisteSaksbehandlerForTotrinn) {
        this.oppgave = oppgave;
        this.andreKriterierType = andreKriterierType;
        this.sisteSaksbehandlerForTotrinn = sisteSaksbehandlerForTotrinn;
    }

    public Oppgave getOppgave() {
        return oppgave;
    }

    public AndreKriterierType getAndreKriterierType() {
        return andreKriterierType;
    }

    public String getSisteSaksbehandlerForTotrinn() {
        return sisteSaksbehandlerForTotrinn;
    }

    public Boolean getAktiv() {
        return aktiv;
    }

    public void deaktiverOppgaveEgenskap() {
        aktiv = false;
    }

    public void aktiverOppgaveEgenskap() {
        aktiv = true;
    }
}
