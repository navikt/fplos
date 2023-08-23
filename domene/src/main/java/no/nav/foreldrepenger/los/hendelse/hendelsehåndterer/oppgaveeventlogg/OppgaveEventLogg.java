package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;


@Entity(name = "oppgaveEventLogg")
@Table(name = "OPPGAVE_EVENT_LOGG")
public class OppgaveEventLogg extends BaseEntitet {
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

    @Embedded
    private BehandlingId behandlingId;

    public OppgaveEventLogg() {
        //For automatisk generering
    }

    public OppgaveEventLogg(BehandlingId behandlingId,
                            OppgaveEventType eventType,
                            AndreKriterierType andreKriterierType,
                            String behandlendeEnhet,
                            LocalDateTime fristTid) {
        this(behandlingId, eventType, andreKriterierType, behandlendeEnhet);
        this.fristTid = fristTid;
    }

    public OppgaveEventLogg(BehandlingId behandlingId, OppgaveEventType eventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        this.eventType = eventType;
        this.andreKriterierType = andreKriterierType;
        this.behandlendeEnhet = behandlendeEnhet;
        this.behandlingId = behandlingId;
    }

    public static OppgaveEventLogg opprettetOppgaveEvent(Oppgave oppgave) {
        var behandlingId = oppgave.getBehandlingId();
        var behandlendeEnhet = oppgave.getBehandlendeEnhet();
        return new OppgaveEventLogg(behandlingId, OppgaveEventType.OPPRETTET, null, behandlendeEnhet);
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
        return behandlingId;
    }

    public LocalDateTime getFristTid() {
        return fristTid;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "OppgaveEventLogg{" + "id=" + id + ", eventType=" + eventType + ", andreKriterierType=" + andreKriterierType + ", behandlendeEnhet='"
            + behandlendeEnhet + '\'' + ", fristTid=" + fristTid + ", behandlingId=" + behandlingId + '}';
    }

    public static class Builder {
        private OppgaveEventLogg oppgaveEventLoggMal;

        public Builder() {
            oppgaveEventLoggMal = new OppgaveEventLogg();
        }

        public Builder behandlendeEnhet(String behandlendeEnhet) {
            oppgaveEventLoggMal.behandlendeEnhet = behandlendeEnhet;
            return this;
        }

        public Builder behandlingId(BehandlingId behandlingId) {
            oppgaveEventLoggMal.behandlingId = behandlingId;
            return this;
        }

        public Builder type(OppgaveEventType type) {
            oppgaveEventLoggMal.eventType = type;
            return this;
        }

        public Builder andreKriterierType(AndreKriterierType andreKriterierType) {
            oppgaveEventLoggMal.andreKriterierType = andreKriterierType;
            return this;
        }

        public Builder fristTid(LocalDateTime fristTid) {
            oppgaveEventLoggMal.fristTid = fristTid;
            return this;
        }

        public OppgaveEventLogg build() {
            verifyStateForBuild();
            return oppgaveEventLoggMal;
        }

        public void verifyStateForBuild() {
            Objects.requireNonNull(oppgaveEventLoggMal.behandlingId, "behandlingId");
            Objects.requireNonNull(oppgaveEventLoggMal.eventType, "eventType");
            Objects.requireNonNull(oppgaveEventLoggMal.behandlendeEnhet, "behandlendeEnhet");
        }

    }
}
