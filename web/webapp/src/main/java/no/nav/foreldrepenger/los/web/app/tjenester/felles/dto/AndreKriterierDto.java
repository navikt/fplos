package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringAndreKriterierType;

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