package no.nav.foreldrepenger.los.reservasjon;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveBehandlingStatus;

public record OppgaveBehandlingStatusWrapper(Oppgave oppgave, OppgaveBehandlingStatus status) {
}
