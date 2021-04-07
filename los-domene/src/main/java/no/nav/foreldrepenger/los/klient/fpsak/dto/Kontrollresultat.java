package no.nav.foreldrepenger.los.klient.fpsak.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Kontrollresultat {

    HOY,
    IKKE_HOY,
    IKKE_KLASSIFISERT;

    @JsonCreator
    public static Kontrollresultat fraKode(@JsonProperty("kode") String kode) {
        return kode.equals("-") ? null : valueOf(kode);
    }
}
