package no.nav.fplos.foreldrepengerbehandling.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum UtsettelseÅrsak {
    ARBEID, LOVBESTEMT_FERIE, SYKDOM, INSTITUSJON_SØKER, INSTITUSJON_BARNET;

    @JsonCreator
    public static UtsettelseÅrsak fraKode(@JsonProperty("kode") String kode) {
        return kode.equals("-") ? null : valueOf(kode);
    }

    boolean gjelderSykdom() {
        return this.equals(SYKDOM) ||
                this.equals(INSTITUSJON_SØKER) ||
                this.equals(INSTITUSJON_BARNET);
    }

}
