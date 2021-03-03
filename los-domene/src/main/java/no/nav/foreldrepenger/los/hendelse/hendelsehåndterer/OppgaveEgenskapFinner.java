package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

import java.util.List;

public interface OppgaveEgenskapFinner {
    List<AndreKriterierType> getAndreKriterier();

    String getSaksbehandlerForTotrinn();
}
