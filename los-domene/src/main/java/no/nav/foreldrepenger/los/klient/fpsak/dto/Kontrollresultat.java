package no.nav.foreldrepenger.los.klient.fpsak.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Kontrollresultat {

    HØY("HOY"),
    IKKE_HØY("IKKE_HOY"),
    IKKE_KLASSIFISERT("IKKE_KLASSIFISERT"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private String kode;

    Kontrollresultat(String kode) {
        this.kode = kode;
    }
}
