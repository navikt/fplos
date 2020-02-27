package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;

import java.util.List;

public class OppgaveHistorikk {
    private final boolean erSisteEventÅpningsevent;
    private final OppgaveEventLogg sisteÅpningsEvent;

    public OppgaveHistorikk(List<OppgaveEventLogg> oppgaveEventLogg) {
        this.erSisteEventÅpningsevent = !oppgaveEventLogg.isEmpty()
                && oppgaveEventLogg.get(0).getEventType().erÅpningsevent();
        this.sisteÅpningsEvent = sisteÅpningsEventFra(oppgaveEventLogg);
    }

    public boolean erSisteEventÅpningsevent() {
        return erSisteEventÅpningsevent;
    }

    public OppgaveEventLogg getSisteÅpningsEvent() {
        return sisteÅpningsEvent;
    }

    private static OppgaveEventLogg sisteÅpningsEventFra(List<OppgaveEventLogg> oppgaveEventLogg) {
        return oppgaveEventLogg.stream()
                .filter(e -> e.getEventType().equals(OppgaveEventType.OPPRETTET))
                .findFirst()
                .orElse(null);
    }
}
