package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import java.util.List;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

public interface OppgaveEgenskapFinner {
    List<AndreKriterierType> getAndreKriterier();

    String getSaksbehandlerForTotrinn();
}
