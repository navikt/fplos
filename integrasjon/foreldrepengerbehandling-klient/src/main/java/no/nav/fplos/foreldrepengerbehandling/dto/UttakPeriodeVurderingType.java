package no.nav.fplos.foreldrepengerbehandling.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public enum UttakPeriodeVurderingType {
    PERIODE_OK, PERIODE_OK_ENDRET, PERIODE_KAN_IKKE_AVKLARES, PERIODE_IKKE_VURDERT;

    @JsonCreator
    public static UttakPeriodeVurderingType fraKode(@JsonProperty("kode") String kode) {
        return kode.equals("-") ? null : valueOf(kode);
    }

    boolean erOmsøktOgIkkeAvklart() {
        return this.equals(PERIODE_IKKE_VURDERT);
    }
}
