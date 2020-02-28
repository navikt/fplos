package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;

import java.util.List;

public class OppgaveHistorikk {
    private final boolean erUtenHistorikk;
    private final boolean erSisteEventÅpningsevent;
    private final OppgaveEventLogg sisteÅpningsEvent;

    public OppgaveHistorikk(List<OppgaveEventLogg> oppgaveEventLogg) {
        this.erUtenHistorikk = oppgaveEventLogg.isEmpty();
        this.sisteÅpningsEvent = sisteÅpningsEventFra(oppgaveEventLogg);
        this.erSisteEventÅpningsevent = !oppgaveEventLogg.isEmpty()
                && oppgaveEventLogg.get(0).getEventType().erÅpningsevent();
    }

    public boolean erSisteEventÅpningsevent() {
        return erSisteEventÅpningsevent;
    }

    public boolean erUtenHistorikk() {
        return erUtenHistorikk;
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

    public boolean erSisteÅpningsEventKriterie(AndreKriterierType kriterie) {
        return sisteÅpningsEvent != null
                && sisteÅpningsEvent.getAndreKriterierType() == kriterie;
    }

    public boolean erSammeEnhet(String enhet) {
        return sisteÅpningsEvent != null
                && sisteÅpningsEvent.getBehandlendeEnhet().equals(enhet);
    }
}
