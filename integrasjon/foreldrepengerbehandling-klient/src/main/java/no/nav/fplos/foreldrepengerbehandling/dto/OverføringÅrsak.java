package no.nav.fplos.foreldrepengerbehandling.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OverføringÅrsak {
    INSTITUSJONSOPPHOLD_ANNEN_FORELDER, SYKDOM_ANNEN_FORELDER, IKKE_RETT_ANNEN_FORELDER, ALENEOMSORG;

    @JsonCreator
    public OverføringÅrsak fraKode(String kode) {
        return kode.equals("-") ? null : valueOf(kode);
    }

    boolean gjelderSykdom() {
        return this.equals(OverføringÅrsak.SYKDOM_ANNEN_FORELDER) ||
                this.equals(OverføringÅrsak.INSTITUSJONSOPPHOLD_ANNEN_FORELDER);
    }
}
