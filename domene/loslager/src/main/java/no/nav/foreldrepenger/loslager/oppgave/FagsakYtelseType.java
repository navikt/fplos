package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FagsakYtelseType implements Kodeverdi {
    ENGANGSTØNAD("ES", "Engangsstønad"),
    FORELDREPENGER("FP", "Foreldrepenger"),
    SVANGERSKAPSPENGER("SVP", "Svangerskapspenger");

    public static final String KODEVERK = "FAGSAK_YTELSE_TYPE";
    private final String kode;
    private final String navn;
    private static final Map<String, FagsakYtelseType> kodeMap = Collections.unmodifiableMap(initializeMapping());

    FagsakYtelseType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    private static HashMap<String, FagsakYtelseType> initializeMapping() {
        HashMap<String, FagsakYtelseType> map = new HashMap<>();
        for (var v : values()) {
            map.putIfAbsent(v.kode, v);
        }
        return map;
    }

    @JsonCreator
    public static FagsakYtelseType fraKode(@JsonProperty("kode") String kode) {
        return Optional.ofNullable(kodeMap.get(kode))
                .orElse(null);
    }

    public static List<FagsakYtelseType> getEnums() {
        return Arrays.stream(values())
                .collect(Collectors.toList());
    }

    public static Map<String, FagsakYtelseType> kodeMap() {
        return Collections.unmodifiableMap(kodeMap());
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
