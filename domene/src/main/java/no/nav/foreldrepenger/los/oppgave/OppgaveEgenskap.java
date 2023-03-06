package no.nav.foreldrepenger.los.oppgave;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "OppgaveEgenskap")
@Table(name = "OPPGAVE_EGENSKAP")
public class OppgaveEgenskap extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPPGAVE_EGENSKAP")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_ID", nullable = false)
    private Oppgave oppgave;

    @Column(name = "OPPGAVE_ID", updatable = false, insertable = false)
    private Long oppgaveId;

    @Convert(converter = AndreKriterierType.KodeverdiConverter.class)
    @Column(name = "ANDRE_KRITERIER_TYPE", nullable = false)
    private AndreKriterierType andreKriterierType;

    @Column(name = "SISTE_SAKSBEHANDLER_FOR_TOTR")
    private String sisteSaksbehandlerForTotrinn;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "AKTIV")
    private Boolean aktiv = Boolean.TRUE;

    public OppgaveEgenskap() {
        //CDI
    }

    public OppgaveEgenskap(Oppgave oppgave, AndreKriterierType andreKriterierType) {
        this.oppgave = oppgave;
        this.andreKriterierType = andreKriterierType;
    }

    public OppgaveEgenskap(Oppgave oppgave, AndreKriterierType type, String sisteSaksbehandlerForTotrinn) {
        this.oppgave = oppgave;
        this.andreKriterierType = type;
        this.sisteSaksbehandlerForTotrinn = sisteSaksbehandlerForTotrinn;
    }

    public OppgaveEgenskap beslutterEgenskapFra(Oppgave oppgave, String sisteSaksbehandlerForTotrinn) {
        return new OppgaveEgenskap(oppgave, AndreKriterierType.TIL_BESLUTTER, sisteSaksbehandlerForTotrinn);
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

    public void setSisteSaksbehandlerForTotrinn(String sisteSaksbehandlerForTotrinn) {
        this.sisteSaksbehandlerForTotrinn = sisteSaksbehandlerForTotrinn;
    }
}
