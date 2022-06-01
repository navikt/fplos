package no.nav.foreldrepenger.los.klient.fpsak.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OverføringÅrsak {

    INSTITUSJONSOPPHOLD_ANNEN_FORELDER("INSTITUSJONSOPPHOLD_ANNEN_FORELDER"),
    SYKDOM_ANNEN_FORELDER("SYKDOM_ANNEN_FORELDER"),
    IKKE_RETT_ANNEN_FORELDER("IKKE_RETT_ANNEN_FORELDER"),
    ALENEOMSORG("ALENEOMSORG"),
    UDEFINERT("-")
    ;
    @JsonValue
    private String kode;

    OverføringÅrsak(String kode) {
        this.kode = kode;
    }

    boolean gjelderSykdom() {
        return this.equals(OverføringÅrsak.SYKDOM_ANNEN_FORELDER) ||
                this.equals(OverføringÅrsak.INSTITUSJONSOPPHOLD_ANNEN_FORELDER);
    }
}
