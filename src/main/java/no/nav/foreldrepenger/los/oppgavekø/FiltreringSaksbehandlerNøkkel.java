package no.nav.foreldrepenger.los.oppgavekø;

import jakarta.persistence.Embeddable;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;

@Embeddable
public record FiltreringSaksbehandlerNøkkel(Saksbehandler saksbehandler, OppgaveFiltrering oppgaveFiltrering) {
}
