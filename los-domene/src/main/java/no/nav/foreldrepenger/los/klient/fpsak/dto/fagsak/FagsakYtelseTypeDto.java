package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum FagsakYtelseTypeDto {
    ENGANGSTØNAD("ES", "Engangsstønad"),
    FORELDREPENGER("FP", "Foreldrepenger"),
    SVANGERSKAPSPENGER("SVP", "Svangerskapspenger");

    public static final String KODEVERK = "FAGSAK_YTELSE";

    private static final Map<String, FagsakYtelseTypeDto> KODEVERDI_MAP = Map.of(
            ENGANGSTØNAD.getKode(), ENGANGSTØNAD,
            FORELDREPENGER.getKode(), FORELDREPENGER,
            SVANGERSKAPSPENGER.getKode(), SVANGERSKAPSPENGER
    );
    
    private final String kode;
    private final String navn;

    FagsakYtelseTypeDto(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static FagsakYtelseTypeDto fraKode(@JsonProperty("kode") String kode) {
        return Optional.ofNullable(kode)
                .map(KODEVERDI_MAP::get)
                .orElseThrow(() -> new IllegalArgumentException("Ukjent FagsakYtelseType: " + kode));
    }

    public String getKode() { return kode; }

    public String getNavn() {
        return navn;
    }

    public String getKodeverk() {
        return KODEVERK;
    }

}
