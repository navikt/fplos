package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum BehandlingType {
    FØRSTEGANGSSØKNAD ("BT-002"),
    KLAGE("BT-003"),
    REVURDERING("BT-004"),
    INNSYN("BT-006"),
    ANKEe("BT-008");

    private String value;

    private static final Map<String, BehandlingType> kodeMap = Collections.unmodifiableMap(initializeMapping());

    private static HashMap<String, BehandlingType> initializeMapping() {
        HashMap<String, BehandlingType> map = new HashMap<>();
        for (var v : values()) {
            map.putIfAbsent(v.value, v);
        }
        return map;
    }

    BehandlingType(String value) {
        this.value = value;
    }

    public static BehandlingType fraKode(String value) {
        return Optional.ofNullable(kodeMap.get(value))
                .orElse(null);
    }

    public String getKode() { return value; }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<BehandlingType, String> {
        @Override
        public String convertToDatabaseColumn(BehandlingType attribute) {
            return Optional.ofNullable(attribute)
                    .map(BehandlingType::getKode)
                    .orElse(null);
        }

        @Override
        public BehandlingType convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData)
                    .map(BehandlingType::fraKode)
                    .orElse(null);
        }
    }

}
