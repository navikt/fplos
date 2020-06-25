package no.nav.fplos.kafkatjenester;

import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.loslager.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;

public class TilbakekrevingOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier;
    private final String saksbehandlerForTotrinn;

    public TilbakekrevingOppgaveEgenskapFinner(List<Aksjonspunkt> aksjonspunkter, String saksbehandler) {
        this.andreKriterier = fra(aksjonspunkter);
        this.saksbehandlerForTotrinn = saksbehandler;
    }

    private List<AndreKriterierType> fra(List<Aksjonspunkt> aksjonspunkter) {
        if (aksjonspunkter.stream().anyMatch(a -> a.getKode().equals("5005") && a.erOpprettet())) {
            return Collections.singletonList(AndreKriterierType.TIL_BESLUTTER);
        }
        return Collections.emptyList();
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }
}
