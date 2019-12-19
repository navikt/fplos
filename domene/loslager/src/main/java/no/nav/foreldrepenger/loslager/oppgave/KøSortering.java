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

    BEHANDLINGSFRIST("BEHFRIST", "Dato for behandlingsfrist"),
    OPPRETT_BEHANDLING("OPPRBEH", "Dato for opprettelse av behandling"),
    FORSTE_STONADSDAG("FORSTONAD", "Dato for første stønadsdag"),
    BELOP("BELOP", "Beløp", "HELTALL", "TILBAKEKREVING"),
    UTLOPSFRIST("UTLOPSFRIST", "Utløpsfrist", "DATO", "TILBAKEKREVING");

    @JsonProperty("kode")
    private String kode;
    @JsonProperty("navn")
    private final String navn;
    @JsonProperty("felttype")
    private final String felttype;
    @JsonProperty("feltkategori")
    private final String feltkategori;

    public static final String KODEVERK = "KO_SORTERING";
    public static final String FT_HELTALL = "HELTALL";
    public static final String FT_DATO = "DATO";

    public static final String FK_UNIVERSAL = "UNIVERSAL";
    public static final String FK_TILBAKEKREVING = "TILBAKEKREVING";

    private static final Map<String, KøSortering> kodeMap = Collections.unmodifiableMap(initializeMapping());

    KøSortering(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
        this.felttype = FT_DATO;
        this.feltkategori = FK_UNIVERSAL;
    }

    KøSortering(String kode, String navn, String felttype, String feltkategori) {
        this.kode = kode;
        this.navn = navn;
        this.felttype = felttype;
        this.feltkategori = feltkategori;
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

    public String getFelttype() {
        return felttype;
    }

    public String getFeltkategori() {
        return feltkategori;
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
    static KøSortering findValue(@JsonProperty("kode") String kode) {
        return fraKode(kode);
    }

}
