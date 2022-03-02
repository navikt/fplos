package no.nav.foreldrepenger.los.oppgave;

import java.util.Map;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum FagsakYtelseType implements Kodeverdi {
    ENGANGSTØNAD("ES", "Engangsstønad"),
    FORELDREPENGER("FP", "Foreldrepenger"),
    SVANGERSKAPSPENGER("SVP", "Svangerskapspenger");

    public static final String KODEVERK = "FAGSAK_YTELSE_TYPE";
    private static final Map<String, FagsakYtelseType> KODEVERDI_MAP = Map.of(
            ENGANGSTØNAD.getKode(), ENGANGSTØNAD,
            FORELDREPENGER.getKode(), FORELDREPENGER,
            SVANGERSKAPSPENGER.getKode(), SVANGERSKAPSPENGER
    );
    @JsonValue
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

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<FagsakYtelseType, String> {
        @Override
        public String convertToDatabaseColumn(FagsakYtelseType attribute) {
            return Optional.ofNullable(attribute)
                .map(FagsakYtelseType::getKode)
                .orElse(null);
        }

        @Override
        public FagsakYtelseType convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData)
                    .map(FagsakYtelseType::fraKode)
                    .orElse(null);
        }
    }

}
