package no.nav.foreldrepenger.los.reservasjon;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;

import java.time.LocalDateTime;

public record SisteReserverteMetadata(Long oppgaveId, OppgaveEventType sisteEventType, LocalDateTime aktuellTid) {
}
