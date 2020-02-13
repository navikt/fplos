package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public class OppgaveEventLoggDto { //TODO FRIST_TID legges inn her?

    private Long behandlingId;
    private UUID eksternId;
    private OppgaveEventType eventType;
    private AndreKriterierType andreKriterierType; // @TODO: vurder om ukjent-typen må inn = AndreKriterierType.UKJENT;
    private String behandlendeEnhet;
    private LocalDateTime opprettetTidspunkt; // NOSONAR

    public OppgaveEventLoggDto(OppgaveEventLogg o) {
        this.behandlingId = o.getBehandlingId();
        this.eventType = o.getEventType();
        this.andreKriterierType = o.getAndreKriterierType();
        this.behandlendeEnhet = o.getBehandlendeEnhet();
        this.opprettetTidspunkt = o.getOpprettetTidspunkt();
        this.eksternId = o.getEksternId();
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

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public UUID getEksternId() {
        return eksternId;
    }
}
