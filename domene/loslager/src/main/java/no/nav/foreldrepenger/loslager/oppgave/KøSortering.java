package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Optional;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KøSortering implements Kodeverdi {

    BEHANDLINGSFRIST("BEHFRIST", "Dato for behandlingsfrist"),
    OPPRETT_BEHANDLING("OPPRBEH", "Dato for opprettelse av behandling"),
    FORSTE_STONADSDAG("FORSTONAD", "Dato for første stønadsdag"),
    BELOP("BELOP", "Feilutbetalt beløp", "HELTALL", "TILBAKEKREVING"),
    FEILUTBETALINGSTART("FEILUTBETALINGSTART", "Dato for første feilutbetaling", "DATO", "TILBAKEKREVING");

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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static KøSortering fraKode(@JsonProperty("kode") String kode) {
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ukjent KøSortering: " + kode));
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
}
