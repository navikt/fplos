package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;

import java.util.Comparator;
import java.util.List;

public class OppgaveHistorikk {
    private final boolean erUtenHistorikk;
    private final OppgaveEventLogg sisteOpprettetEvent;
    private final OppgaveEventType sisteEventType;

    public OppgaveHistorikk(List<OppgaveEventLogg> oppgaveEventLogg) {
        this.erUtenHistorikk = oppgaveEventLogg.isEmpty();
        this.sisteOpprettetEvent = sisteÅpningsEventFra(oppgaveEventLogg);
        this.sisteEventType = oppgaveEventLogg.isEmpty() ? null : oppgaveEventLogg.get(0).getEventType();
    }

    public boolean erÅpenOppgave() {
        return sisteEventType != null && sisteEventType.erÅpningsevent();
    }

    public boolean erSisteEventLukkeevent() {
        return sisteEventType != null && sisteEventType.equals(OppgaveEventType.LUKKET);
    }

    public boolean erSisteVenteEvent() {
        return sisteEventType != null && sisteEventType.erVenteEvent();
    }

    public boolean erUtenHistorikk() {
        return erUtenHistorikk;
    }

    public OppgaveEventLogg getSisteOpprettetEvent() {
        return sisteOpprettetEvent;
    }

    public boolean erSisteOppgaveTilBeslutter() {
        return sisteOpprettetEvent != null && sisteOpprettetEvent.getAndreKriterierType().erTilBeslutter();
    }

    public boolean erSisteOppgavePapirsøknad() {
        return sisteOpprettetEvent != null && sisteOpprettetEvent.getAndreKriterierType().equals(AndreKriterierType.PAPIRSØKNAD);
    }

    public boolean erSisteOppgaveRegistrertPåEnhet(String enhet) {
        return sisteOpprettetEvent != null
                && sisteOpprettetEvent.getBehandlendeEnhet().equals(enhet);
    }

    private static OppgaveEventLogg sisteÅpningsEventFra(List<OppgaveEventLogg> oppgaveEventLogg) {
        return oppgaveEventLogg.stream()
                .filter(e -> e.getEventType().equals(OppgaveEventType.OPPRETTET))
                .max(Comparator.comparing(OppgaveEventLogg::getOpprettetTidspunkt))
                .orElse(null);
    }
}
