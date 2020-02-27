package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;

import java.util.List;

public interface OppgaveEgenskapFinner {
    List<AndreKriterierType> getAndreKriterier();

    String getSaksbehandlerForTotrinn();
}
