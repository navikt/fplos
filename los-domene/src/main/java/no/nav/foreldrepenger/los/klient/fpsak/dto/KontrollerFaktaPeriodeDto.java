package no.nav.foreldrepenger.los.klient.fpsak.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KontrollerFaktaPeriodeDto {

    private OverføringÅrsak overføringÅrsak;
    private UtsettelseÅrsak utsettelseÅrsak;
    private UttakPeriodeVurderingType resultat;
    private BigDecimal arbeidstidsprosent;

    @JsonCreator
    public KontrollerFaktaPeriodeDto(@JsonProperty("overføringÅrsak") OverføringÅrsak overføringÅrsak,
                                     @JsonProperty("utsettelseÅrsak") UtsettelseÅrsak utsettelseÅrsak,
                                     @JsonProperty("resultat") UttakPeriodeVurderingType resultat,
                                     @JsonProperty("arbeidstidsprosent") BigDecimal arbeidstidsprosent) {
        this.overføringÅrsak = overføringÅrsak;
        this.utsettelseÅrsak = utsettelseÅrsak;
        this.resultat = resultat;
        this.arbeidstidsprosent = arbeidstidsprosent;
    }

    public BigDecimal getArbeidstidsprosent() {
        return arbeidstidsprosent;
    }

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
