package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FagsakYtelseTypeDto {
    ENGANGSTØNAD("ES", "Engangsstønad"),
    FORELDREPENGER("FP", "Foreldrepenger"),
    SVANGERSKAPSPENGER("SVP", "Svangerskapspenger");

    @JsonValue
    private final String kode;
    private final String navn;

    FagsakYtelseTypeDto(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public String getKode() { return kode; }

    public String getNavn() {
        return navn;
    }

}
