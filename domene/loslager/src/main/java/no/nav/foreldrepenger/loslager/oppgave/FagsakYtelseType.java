package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Optional;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FagsakYtelseType implements Kodeverdi {
    ENGANGSTØNAD("ES", "Engangsstønad"),
    FORELDREPENGER("FP", "Foreldrepenger"),
    SVANGERSKAPSPENGER("SVP", "Svangerskapspenger");

    public static final String KODEVERK = "FAGSAK_YTELSE_TYPE";
    private final String kode;
    private final String navn;

    FagsakYtelseType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static FagsakYtelseType fraKode(@JsonProperty("kode") String kode) {
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
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
