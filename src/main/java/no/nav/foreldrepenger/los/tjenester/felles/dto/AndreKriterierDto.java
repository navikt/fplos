package no.nav.foreldrepenger.los.tjenester.felles.dto;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;

import java.util.List;

public record AndreKriterierDto(AndreKriterierType andreKriterierType, boolean inkluder) {

    public AndreKriterierDto(FiltreringAndreKriterierType fakt) {
        this(fakt.getAndreKriterierType(), fakt.isInkluder());
    }

    public static List<AndreKriterierDto> listeFra(List<FiltreringAndreKriterierType> filtreringAndreKriterierTyper) {
        return filtreringAndreKriterierTyper.stream()
            .map(AndreKriterierDto::new)
            .toList();
    }
}
