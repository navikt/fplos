package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

public record OppgaveEventLoggDto(BehandlingId behandlingId, OppgaveEventType eventType, AndreKriterierType andreKriterierType,
                                  String behandlendeEnhet, LocalDateTime opprettetTidspunkt) {

    public OppgaveEventLoggDto(OppgaveEventLogg o) {
        this(o.getBehandlingId(), o.getEventType(), o.getAndreKriterierType(), o.getBehandlendeEnhet(), o.getOpprettetTidspunkt());
    }
}
