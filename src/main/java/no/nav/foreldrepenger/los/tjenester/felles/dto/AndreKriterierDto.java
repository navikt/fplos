package no.nav.foreldrepenger.los.tjenester.felles.dto;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;

public class AndreKriterierDto {

    private AndreKriterierType andreKriterierType;
    private boolean inkluder;

    public AndreKriterierDto(FiltreringAndreKriterierType filtreringandrekrit) {
        this.andreKriterierType = filtreringandrekrit.getAndreKriterierType();
        this.inkluder = filtreringandrekrit.isInkluder();
    }

    public AndreKriterierType getAndreKriterierType() {
        return andreKriterierType;
    }

    public boolean isInkluder() {
        return inkluder;
    }
}
