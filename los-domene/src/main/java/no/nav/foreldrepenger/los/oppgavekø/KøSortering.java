package no.nav.foreldrepenger.los.oppgavekø;

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum KøSortering implements Kodeverdi {

    BEHANDLINGSFRIST("BEHFRIST", "Dato for behandlingsfrist"),
    OPPRETT_BEHANDLING("OPPRBEH", "Dato for opprettelse av behandling"),
    FØRSTE_STØNADSDAG("FORSTONAD", "Dato for første stønadsdag"),
    BELØP("BELOP", "Feilutbetalt beløp", "HELTALL", "TILBAKEKREVING"),
    FEILUTBETALINGSTART("FEILUTBETALINGSTART", "Dato for første feilutbetaling", "DATO", "TILBAKEKREVING");

    @JsonValue
    private String kode;
    private final String navn;
    private final String felttype;
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
                    .map(KodeverdiConverter::fraKode)
                    .orElse(null);
        }

        private static KøSortering fraKode(String kode) {
            return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ukjent KøSortering: " + kode));
        }
    }
}
