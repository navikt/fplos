package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum FagsakYtelseType {
    ENGANGSTØNAD("ES"),
    FORELDREPENGER("FP"),
    SVANGERSKAPSPENGER("SVP");

    private String value;

    private static final Map<String, FagsakYtelseType> kodeMap = Collections.unmodifiableMap(initializeMapping());

    private static HashMap<String, FagsakYtelseType> initializeMapping() {
        HashMap<String, FagsakYtelseType> map = new HashMap<>();
        for (var v : values()) {
            map.putIfAbsent(v.value, v);
        }
        return map;
    }

    FagsakYtelseType(String value) {
        this.value = value;
    }

    public static FagsakYtelseType fraKode(String value) {
        return Optional.ofNullable(kodeMap.get(value))
                .orElse(null);
    }

    public String getKode() { return value; }

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
