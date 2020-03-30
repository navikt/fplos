package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Optional;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AndreKriterierType implements Kodeverdi {

    TIL_BESLUTTER("TIL_BESLUTTER", "Til beslutter"),
    PAPIRSØKNAD("PAPIRSOKNAD", "Registrer papirsøknad"),
    UTBETALING_TIL_BRUKER("UTBETALING_TIL_BRUKER", "Utbetaling til bruker"),
    UTLANDSSAK("UTLANDSSAK", "Utland"),
    SOKT_GRADERING("SOKT_GRADERING", "Søkt gradering"),
    VURDER_SYKDOM("VURDER_SYKDOM", "Vurder sykdom"),
    VURDER_FARESIGNALER("VURDER_FARESIGNALER", "Vurder faresignaler"),
    BERØRT_BEHANDLING("BERØRT_BEHANDLING", "Berørt behandling");
    //SELVSTENDIG_FRILANSER("SELVSTENDIG_FRILANSER", "Selvstendig næringsdrivende eller frilanser")

    private String kode;
    private final String navn;
    public static final String KODEVERK = "ANDRE_KRITERIER";

    AndreKriterierType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
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

    public boolean erTilBeslutter() {
        return this.equals(TIL_BESLUTTER);
    }

    @JsonCreator
    public static AndreKriterierType fraKode(@JsonProperty("kode") String kode) {
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ukjent AndreKriterierType: " + kode));
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
}
