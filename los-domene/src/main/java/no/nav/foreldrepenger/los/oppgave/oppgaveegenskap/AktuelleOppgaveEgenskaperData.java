package no.nav.foreldrepenger.los.oppgave.oppgaveegenskap;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AktuelleOppgaveEgenskaperData {
    private final List<AndreKriterierType> andreKriterierTyper;
    private final String ansvarligSaksbehandlerIdent;

    public AktuelleOppgaveEgenskaperData(String ansvarligSaksbehandlerIdent, List<AndreKriterierType> egenskaper) {
        this.andreKriterierTyper = Collections.unmodifiableList(egenskaper);
        this.ansvarligSaksbehandlerIdent = ansvarligSaksbehandlerIdent;
    }

    public List<AndreKriterierType> getAndreKriterierTyper() {
        return andreKriterierTyper;
    }

    public String getAnsvarligSaksbehandlerIdent() {
        return ansvarligSaksbehandlerIdent;
    }
}
