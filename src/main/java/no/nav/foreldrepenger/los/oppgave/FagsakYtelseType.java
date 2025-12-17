package no.nav.foreldrepenger.los.oppgave;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;
import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum FagsakYtelseType implements Kodeverdi {
    ENGANGSTØNAD("ES", "Engangsstønad"),
    FORELDREPENGER("FP", "Foreldrepenger"),
    SVANGERSKAPSPENGER("SVP", "Svangerskapspenger");

    @JsonValue
    @EnumeratedValue
    private final String kode;
    private final String navn;

    FagsakYtelseType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public static FagsakYtelseType fraKode(String kode) {
        if (kode == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(v -> v.kode.equals(kode))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ukjent FagsakYtelseType: " + kode));
    }

    public String getKode() {
        return kode;
    }

    public String getNavn() {
        return navn;
    }

}
