package no.nav.fplos.foreldrepengerbehandling.dto.fagsak;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public enum FagsakYtelseTypeDto implements FpsakKodeverdi {
    ENGANGSTØNAD("ES", "Engangsstønad"),
    FORELDREPENGER("FP", "Foreldrepenger"),
    SVANGERSKAPSPENGER("SVP", "Svangerskapspenger");

    public static final String KODEVERK = "FAGSAK_YTELSE_TYPE";
    private final String kode;
    private final String navn;

    FagsakYtelseTypeDto(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static FagsakYtelseTypeDto fraKode(@JsonProperty("kode") String kode) {
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ukjent FagsakYtelseTypeDto: " + kode));
    }

    public String getKode() { return kode; }

    public String getNavn() {
        return navn;
    }

    public String getKodeverk() {
        return KODEVERK;
    }

}
