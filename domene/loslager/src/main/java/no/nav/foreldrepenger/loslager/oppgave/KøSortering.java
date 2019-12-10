package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public enum KøSortering implements Kodeverdi {

    BEHANDLINGSFRIST("BEHFRIST", "Dato for første stønadsdag"),
    OPPRETT_BEHANDLING("OPPRBEH", "Dato for første stønadsdag"),
    FORSTE_STONADSDAG("FORSTONAD", "Dato for første stønadsdag");

    @JsonProperty("kode")
    private String kode;
    @JsonProperty("navn")
    private final String navn;
    public static final String KODEVERK = "KO_SORTERING";
    private static final Map<String, KøSortering> kodeMap = Collections.unmodifiableMap(initializeMapping());

    KøSortering(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    private static HashMap<String, KøSortering> initializeMapping() {
        HashMap<String, KøSortering> map = new HashMap<>();
        for (var v : values()) {
            map.putIfAbsent(v.kode, v);
        }
        return map;
    }


    public static KøSortering fraKode(String value) {
        return Optional.ofNullable(kodeMap.get(value))
                .orElse(null);
    }

    public static List<KøSortering> getEnums() {
        return Arrays.stream(values())
                .collect(Collectors.toList());
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }

    public String getKodeverk() {
        return KODEVERK;
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<KøSortering, String> {
        @Override
        public String convertToDatabaseColumn(KøSortering attribute) {
            return Optional.ofNullable(attribute)
                    .map(KøSortering::getKode)
                    .orElse(null);
        }

        @Override
        public KøSortering convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData)
                    .map(KøSortering::fraKode)
                    .orElse(null);
        }
    }

    @JsonCreator
    static KøSortering findValue(@JsonProperty("kode") String kode,
                                    @JsonProperty("navn") String navn,
                                    @JsonProperty("kodeverk") String kodeverk) {
        return fraKode(kode);
    }
}
