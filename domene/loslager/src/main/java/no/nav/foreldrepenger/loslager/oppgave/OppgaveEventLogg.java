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
import java.util.UUID;

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

    @Column(name = "EKSTERN_ID")
    private UUID eksternId;

    public OppgaveEventLogg(){
        //For automatisk generering
    }

    public OppgaveEventLogg(UUID eksternId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime fristTid) {
        this(eksternId, eventType, andreKriterierType, behandlendeEnhet);
        this.fristTid = fristTid;
    }

    public OppgaveEventLogg(UUID eksternId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet ) {
        this.eventType = eventType;
        this.andreKriterierType = andreKriterierType;
        this.behandlendeEnhet = behandlendeEnhet;
        this.eksternId = eksternId;
    }

    @Deprecated
    public OppgaveEventLogg(UUID eksternId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime fristTid, Long behandlingId) {
        this(eksternId, eventType, andreKriterierType, behandlendeEnhet, behandlingId);
        this.fristTid = fristTid;
    }

    @Deprecated
    public OppgaveEventLogg(UUID eksternId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, Long behandlingId ) {
        this.behandlingId = behandlingId;
        this.eventType = eventType;
        this.andreKriterierType = andreKriterierType;
        this.behandlendeEnhet = behandlendeEnhet;
        this.eksternId = eksternId;
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

    public UUID getEksternId() {
        return eksternId;
    }

    public LocalDateTime getFristTid() {
        return fristTid;
    }
}
