package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;

public class OppgaveEventLoggDto {

    private BehandlingId behandlingId;
    private OppgaveEventType eventType;
    private AndreKriterierType andreKriterierType;
    private String behandlendeEnhet;
    private LocalDateTime opprettetTidspunkt;

    public OppgaveEventLoggDto(OppgaveEventLogg o) {
        this.eventType = o.getEventType();
        this.andreKriterierType = o.getAndreKriterierType();
        this.behandlendeEnhet = o.getBehandlendeEnhet();
        this.opprettetTidspunkt = o.getOpprettetTidspunkt();
        this.behandlingId = o.getBehandlingId();
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

    public UUID getBehandlingId() {
        return behandlingId.toUUID();
    }
}
