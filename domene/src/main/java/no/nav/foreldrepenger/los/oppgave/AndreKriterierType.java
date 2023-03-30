package no.nav.foreldrepenger.los.oppgave;

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum AndreKriterierType implements Kodeverdi {

    TIL_BESLUTTER("TIL_BESLUTTER", "Til beslutter"),
    PAPIRSØKNAD("PAPIRSOKNAD", "Registrer papirsøknad"),
    UTBETALING_TIL_BRUKER("UTBETALING_TIL_BRUKER", "Utbetaling til bruker"),
    UTLANDSSAK("UTLANDSSAK", "Bosatt utland"),
    SØKT_GRADERING("SOKT_GRADERING", "Søkt gradering"),
    VURDER_SYKDOM("VURDER_SYKDOM", "Vurder sykdom"),
    PLEIEPENGER("PLEIEPENGER", "Pleiepenger"),
    VURDER_FARESIGNALER("VURDER_FARESIGNALER", "Vurder faresignaler"),
    BERØRT_BEHANDLING("BERØRT_BEHANDLING", "Berørt behandling"),
    ENDRINGSSØKNAD("ENDRINGSSOKNAD", "Endringssøknad"),
    VURDER_FORMKRAV("VURDER_FORMKRAV", "Vurder formkrav"),
    VURDER_EØS_OPPTJENING("VURDER_EØS_OPPTJENING", "Vurder behov for SED"),
    KLAGE_PÅ_TILBAKEBETALING("KLAGE_PÅ_TILBAKEBETALING", "Klage på tilbakebetaling"),
    EØS_SAK("EØS_SAK", "EØS (bosatt Norge)"),
    KODE7_SAK("KODE7_SAK", "Kode 7"),
    SAMMENSATT_KONTROLL("SAMMENSATT_KONTROLL", "Sammensatt kontroll");
    //SELVSTENDIG_FRILANSER("SELVSTENDIG_FRILANSER", "Selvstendig næringsdrivende eller frilanser")

    @JsonValue
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

    public static AndreKriterierType fraKode(String kode) {
        return Arrays.stream(values())
            .filter(v -> v.kode.equals(kode))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ukjent AndreKriterierType: " + kode));
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<AndreKriterierType, String> {
        @Override
        public String convertToDatabaseColumn(AndreKriterierType attribute) {
            return Optional.ofNullable(attribute).map(AndreKriterierType::getKode).orElse(null);
        }

        @Override
        public AndreKriterierType convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData).map(AndreKriterierType::fraKode).orElse(null);
        }


    }
}
