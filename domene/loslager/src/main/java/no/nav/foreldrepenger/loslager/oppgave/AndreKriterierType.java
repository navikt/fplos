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
public enum AndreKriterierType implements Kodeverdi {

    TIL_BESLUTTER("TIL_BESLUTTER", "Til beslutter"),
    PAPIRSØKNAD("PAPIRSOKNAD", "Registrer papirsøknad"),
    UTBETALING_TIL_BRUKER("UTBETALING_TIL_BRUKER", "Utbetaling til bruker"),
    UTLANDSSAK("UTLANDSSAK", "Utland"),
    SOKT_GRADERING("SOKT_GRADERING", "Søkt gradering");

    private String kode;
    private final String navn;
    public static final String KODEVERK = "ANDRE_KRITERIER";

    private static final Map<String, AndreKriterierType> kodeMap = Collections.unmodifiableMap(initializeMapping());

    private static HashMap<String, AndreKriterierType> initializeMapping() {
        HashMap<String, AndreKriterierType> map = new HashMap<>();
        for (var v : values()) {
            map.putIfAbsent(v.kode, v);
        }
        return map;
    }

    AndreKriterierType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public static AndreKriterierType fraKode(String value) {
        return Optional.ofNullable(kodeMap.get(value))
                .orElse(null);
    }

    public static List<AndreKriterierType> getEnums() {
        return Arrays.stream(values())
                .collect(Collectors.toList());
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() { return kode; }

    public String getKodeverk() {
        return KODEVERK;
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<AndreKriterierType, String> {
        @Override
        public String convertToDatabaseColumn(AndreKriterierType attribute) {
            return Optional.ofNullable(attribute)
                    .map(AndreKriterierType::getKode)
                    .orElse(null);
        }

        @Override
        public AndreKriterierType convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData)
                    .map(AndreKriterierType::fraKode)
                    .orElse(null);
        }
    }

    @JsonCreator
    static AndreKriterierType findValue(@JsonProperty("kode") String kode,
                                      @JsonProperty("navn") String navn,
                                      @JsonProperty("kodeverk") String kodeverk) {
        return fraKode(kode);
    }

}
