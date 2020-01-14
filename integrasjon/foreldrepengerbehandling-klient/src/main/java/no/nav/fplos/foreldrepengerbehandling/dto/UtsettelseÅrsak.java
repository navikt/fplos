package no.nav.fplos.foreldrepengerbehandling.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum UtsettelseÅrsak {
    ARBEID, LOVBESTEMT_FERIE, SYKDOM, INSTITUSJONSOPPHOLD_SØKER, INSTITUSJONSOPPHOLD_BARNET;

    @JsonCreator
    public static UtsettelseÅrsak fraKode(@JsonProperty("kode") String kode) {
        return kode.equals("-") ? null : valueOf(kode);
    }

    boolean gjelderSykdom() {
        return this.equals(SYKDOM) ||
                this.equals(INSTITUSJONSOPPHOLD_SØKER) ||
                this.equals(INSTITUSJONSOPPHOLD_BARNET);
    }

}
