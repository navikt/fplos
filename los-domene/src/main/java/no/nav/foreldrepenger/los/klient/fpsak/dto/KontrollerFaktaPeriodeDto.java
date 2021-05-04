package no.nav.foreldrepenger.los.klient.fpsak.dto;

import java.math.BigDecimal;

public record KontrollerFaktaPeriodeDto(OverføringÅrsak overføringÅrsak,
                                        UtsettelseÅrsak utsettelseÅrsak,
                                        UttakPeriodeVurderingType resultat,
                                        BigDecimal arbeidstidsprosent) {

    public boolean gjelderSykdom() {
        return overføringGjelderSykdom() || utsettelseGjelderSykdom();
    }

    private boolean overføringGjelderSykdom() {
        return overføringÅrsak != null && resultat != null
                && overføringÅrsak.gjelderSykdom() && resultat.erOmsøktOgIkkeAvklart();
    }

    private boolean utsettelseGjelderSykdom() {
        return utsettelseÅrsak != null && resultat != null
                && utsettelseÅrsak.gjelderSykdom() && resultat.erOmsøktOgIkkeAvklart();
    }
}
