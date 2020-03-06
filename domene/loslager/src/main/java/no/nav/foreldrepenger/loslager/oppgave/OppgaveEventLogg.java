package no.nav.foreldrepenger.loslager.oppgave;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.foreldrepenger.loslager.BehandlingId;

@Entity(name = "oppgaveEventLogg")
@Table(name = "OPPGAVE_EVENT_LOGG")
public class OppgaveEventLogg extends BaseEntitet{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENTMOTTAK_FEILLOGG")
    private Long id;

    @Column(name = "EVENT_TYPE")
    @Enumerated(EnumType.STRING)
    private OppgaveEventType eventType;

    @Column(name = "ANDRE_KRITERIER_TYPE")
    @Convert(converter = AndreKriterierType.KodeverdiConverter.class)
    private AndreKriterierType andreKriterierType;

    @Column(name = "BEHANDLENDE_ENHET")
    private String behandlendeEnhet;

    @Column(name = "FRIST_TID")
    private LocalDateTime fristTid;

    @Column(name = "EKSTERN_ID", nullable = false)
    private UUID eksternId;

    @Column(name = "BEHANDLING_ID", nullable = false)
    private UUID behandlingId;

    public OppgaveEventLogg(){
        //For automatisk generering
    }

    public OppgaveEventLogg(BehandlingId behandlingId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime fristTid) {
        this(behandlingId, eventType, andreKriterierType, behandlendeEnhet);
        this.fristTid = fristTid;
    }

    public OppgaveEventLogg(BehandlingId behandlingId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet ) {
        this.eventType = eventType;
        this.andreKriterierType = andreKriterierType;
        this.behandlendeEnhet = behandlendeEnhet;
        this.eksternId = behandlingId.toUUID();
        this.behandlingId = behandlingId.toUUID();
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

    public BehandlingId getBehandlingId() {
        return BehandlingId.fromUUID(behandlingId);
    }

    public LocalDateTime getFristTid() {
        return fristTid;
    }
}
