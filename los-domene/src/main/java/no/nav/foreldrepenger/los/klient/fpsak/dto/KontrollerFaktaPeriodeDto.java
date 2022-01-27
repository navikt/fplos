package no.nav.foreldrepenger.los.klient.fpsak.dto;

import java.math.BigDecimal;

public record KontrollerFaktaPeriodeDto(OverføringÅrsak overføringÅrsak,
                                        UtsettelseÅrsak utsettelseÅrsak,
                                        BigDecimal arbeidstidsprosent) {

    public boolean gjelderSykdom() {
        return overføringGjelderSykdom() || utsettelseGjelderSykdom();
    }

    private boolean overføringGjelderSykdom() {
        return overføringÅrsak != null && overføringÅrsak.gjelderSykdom();
    }

    private boolean utsettelseGjelderSykdom() {
        return utsettelseÅrsak != null && utsettelseÅrsak.gjelderSykdom();
    }
}
