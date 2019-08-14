package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;

public class OppgaveEventLoggDto { //TODO FRIST_TID legges inn her?

    private Long behandlingId;
    private OppgaveEventType eventType;
    private AndreKriterierType andreKriterierType = AndreKriterierType.UKJENT;
    private String behandlendeEnhet;
    private LocalDateTime opprettetTidspunkt; // NOSONAR

    public OppgaveEventLoggDto(OppgaveEventLogg o) {
        this.behandlingId = o.getBehandlingId();
        this.eventType = o.getEventType();
        this.andreKriterierType = o.getAndreKriterierType();
        this.behandlendeEnhet = o.getBehandlendeEnhet();
        this.opprettetTidspunkt = o.getOpprettetTidspunkt();
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
}
