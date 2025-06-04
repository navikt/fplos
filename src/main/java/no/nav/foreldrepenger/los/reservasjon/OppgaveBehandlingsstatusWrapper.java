package no.nav.foreldrepenger.los.reservasjon;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveBehandlingsstatus;

public record OppgaveBehandlingsstatusWrapper(Oppgave oppgave, OppgaveBehandlingsstatus status) {
}
