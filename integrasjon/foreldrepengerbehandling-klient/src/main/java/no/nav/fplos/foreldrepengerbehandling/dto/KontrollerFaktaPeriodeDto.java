package no.nav.fplos.foreldrepengerbehandling.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KontrollerFaktaPeriodeDto {

    private OverføringÅrsak overføringÅrsak;
    private UttakPeriodeVurderingType resultat;
    private BigDecimal arbeidstidsprosent;

    @JsonCreator
    public KontrollerFaktaPeriodeDto(@JsonProperty("overføringÅrsak") OverføringÅrsak overføringÅrsak,
                                     @JsonProperty("resultat") UttakPeriodeVurderingType resultat,
                                     @JsonProperty("arbeidstidsprosent") BigDecimal arbeidstidsprosent) {
        this.overføringÅrsak = overføringÅrsak;
        this.resultat = resultat;
        this.arbeidstidsprosent = arbeidstidsprosent;
    }

    public BigDecimal getArbeidstidsprosent() {
        return arbeidstidsprosent;
    }

    public boolean gjelderSykdom() {
        return overføringÅrsak != null && resultat != null &&
                overføringÅrsak.gjelderSykdom() &&
                resultat.erOmsøktOgIkkeAvklart();
    }
}
