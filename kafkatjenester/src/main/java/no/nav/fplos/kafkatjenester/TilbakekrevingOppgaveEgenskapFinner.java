package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TilbakekrevingOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier;
    private final String saksbehandlerForTotrinn;

    public TilbakekrevingOppgaveEgenskapFinner(Map<String, String> aksjonspunkter, String saksbehandler) {
        this.andreKriterier = fra(aksjonspunkter);
        this.saksbehandlerForTotrinn = saksbehandler;
    }

    private List<AndreKriterierType> fra(Map<String, String> aksjonspunkter) {
        if (aksjonspunkter.containsKey("5005") && aksjonspunkter.get("5005").equals("OPPR")) {
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
