package no.nav.foreldrepenger.los.klient.fpsak.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum UtsettelseÅrsak {
    ANNET,
    SYKDOM,
    INSTITUSJONSOPPHOLD_SØKER,
    INSTITUSJONSOPPHOLD_BARNET;

    @JsonCreator
    public static UtsettelseÅrsak fraKode(@JsonProperty("kode") String kode) {
        if (kode == null || kode.equals("-")) {
            return null;
        }
        if (kode.equals(SYKDOM.name())) {
            return SYKDOM;
        }
        if (kode.equals(INSTITUSJONSOPPHOLD_BARNET.name())) {
            return INSTITUSJONSOPPHOLD_BARNET;
        }
        if (kode.equals(INSTITUSJONSOPPHOLD_SØKER.name())) {
            return INSTITUSJONSOPPHOLD_SØKER;
        }
        return ANNET;
    }

    boolean gjelderSykdom() {
        return this.equals(SYKDOM) ||
                this.equals(INSTITUSJONSOPPHOLD_SØKER) ||
                this.equals(INSTITUSJONSOPPHOLD_BARNET);
    }

}
