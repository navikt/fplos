package no.nav.foreldrepenger.loslager.oppgave;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity(name = "oppgaveEventLogg")
@Table(name = "OPPGAVE_EVENT_LOGG")
public class OppgaveEventLogg extends BaseEntitet{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENTMOTTAK_FEILLOGG")
    private Long id;

    @Column(name = "BEHANDLING_ID", nullable = false)
    private Long behandlingId;

    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "EVENT_TYPE", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + OppgaveEventType.DISCRIMINATOR + "'"))
    private OppgaveEventType eventType;

    @ManyToOne
    @JoinColumnOrFormula(column = @JoinColumn(name = "ANDRE_KRITERIER_TYPE", referencedColumnName = "kode"))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + AndreKriterierType.DISCRIMINATOR + "'"))
    private AndreKriterierType andreKriterierType = AndreKriterierType.UKJENT;

    @Column(name = "BEHANDLENDE_ENHET")
    private String behandlendeEnhet;

    @Column(name = "FRIST_TID")
    private LocalDateTime fristTid;

    public OppgaveEventLogg(){
        //For automatisk generering
    }

    public OppgaveEventLogg(Long behandlingId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime fristTid) {
        this(behandlingId, eventType, andreKriterierType, behandlendeEnhet);
        this.fristTid = fristTid;
    }

    public OppgaveEventLogg(Long behandlingId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        this.behandlingId = behandlingId;
        this.eventType = eventType;
        this.andreKriterierType = andreKriterierType;
        this.behandlendeEnhet = behandlendeEnhet;
    }
    public Long getBehandlingId() {
        return behandlingId;
    }

    public OppgaveEventType getEventType() {
        return eventType;
    }

    public AndreKriterierType getAndreKriterierType() {
        return andreKriterierType;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public LocalDateTime getFristTid() {
        return fristTid;
    }
}
