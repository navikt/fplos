package no.nav.foreldrepenger.los.migrering.dto;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

public record OppgaveEgenskapDataDto(
    AndreKriterierType andreKriterierType,
    String sisteSaksbehandlerForTotrinn
) {
}