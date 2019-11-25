package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.fplos.kodeverk.Kodeverdi;

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
public enum BehandlingType implements Kodeverdi {
    FØRSTEGANGSSØKNAD ("BT-002", "Førstegangsbehandling"),
    KLAGE("BT-003", "Klage"),
    REVURDERING("BT-004", "Revurdering"),
    SØKNAD("BT-005", "Søknad"),
    INNSYN("BT-006", "Innsyn"),
    ANKE("BT-008", "Anke");

    private String kode;
    private final String navn;
    public static final String kodeverk = "BEHANDLING_TYPE";

    private static final Map<String, BehandlingType> kodeMap = Collections.unmodifiableMap(initializeMapping());

    private static HashMap<String, BehandlingType> initializeMapping() {
        HashMap<String, BehandlingType> map = new HashMap<>();
        for (var v : values()) {
            map.putIfAbsent(v.kode, v);
        }
        return map;
    }

    BehandlingType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public static BehandlingType fraKode(String kode) {
        return Optional.ofNullable(kodeMap.get(kode))
                .orElse(null);
    }

    public static List<BehandlingType> getEnums() {
        return Arrays.stream(values())
                .collect(Collectors.toList());
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() { return kode; }

    public String getKodeverk() {
        return kodeverk;
    }

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

    @JsonCreator
    static BehandlingType findValue(@JsonProperty("kode") String kode,
                                    @JsonProperty("navn") String navn,
                                    @JsonProperty("kodeverk") String kodeverk) {
        return fraKode(kode);
    }
}
