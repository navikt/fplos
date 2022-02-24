package no.nav.foreldrepenger.los.klient.fpsak.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UtsettelseÅrsak {
    ARBEID("ARBEID"),
    FERIE("LOVBESTEMT_FERIE"),
    SYKDOM("SYKDOM"),
    INSTITUSJON_SØKER("INSTITUSJONSOPPHOLD_SØKER"),
    INSTITUSJON_BARN("INSTITUSJONSOPPHOLD_BARNET"),
    HV_OVELSE("HV_OVELSE"),
    NAV_TILTAK("NAV_TILTAK"),
    FRI("FRI"),
    UDEFINERT("-");

    @JsonValue
    private String kode;

    UtsettelseÅrsak(String kode) {
        this.kode = kode;
    }

    boolean gjelderSykdom() {
        return this.equals(SYKDOM) ||
                this.equals(INSTITUSJON_SØKER) ||
                this.equals(INSTITUSJON_BARN);
    }

}
