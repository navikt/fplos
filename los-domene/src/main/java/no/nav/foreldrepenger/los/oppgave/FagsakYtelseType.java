package no.nav.foreldrepenger.los.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.foreldrepenger.los.felles.Kodeverdi;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.util.Map;
import java.util.Optional;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
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
    private final String kode;
    private final String navn;

    FagsakYtelseType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FagsakYtelseType fraKode(@JsonProperty("kode") String kode) {
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
