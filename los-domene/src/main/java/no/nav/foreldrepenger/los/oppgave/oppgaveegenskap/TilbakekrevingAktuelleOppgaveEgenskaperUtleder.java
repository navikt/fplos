package no.nav.foreldrepenger.los.oppgave.oppgaveegenskap;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

import java.util.Collections;
import java.util.List;

public class TilbakekrevingAktuelleOppgaveEgenskaperUtleder {

    public static AktuelleOppgaveEgenskaperData egenskaperForFptilbake(List<Aksjonspunkt> aksjonspunkter, String ansvarligSaksbehandler) {
        return new AktuelleOppgaveEgenskaperData(ansvarligSaksbehandler, fraTilbakekrevingAksjonspunkter(aksjonspunkter));

    }

    private static List<AndreKriterierType> fraTilbakekrevingAksjonspunkter(List<Aksjonspunkt> aksjonspunkter) {
        if (aksjonspunkter.stream().anyMatch(a -> a.getKode().equals("5005") && a.erOpprettet())) {
            return Collections.singletonList(AndreKriterierType.TIL_BESLUTTER);
        }
        return Collections.emptyList();
    }
}
