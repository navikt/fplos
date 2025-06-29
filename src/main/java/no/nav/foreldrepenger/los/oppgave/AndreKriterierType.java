package no.nav.foreldrepenger.los.oppgave;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum AndreKriterierType implements Kodeverdi {

    TIL_BESLUTTER("TIL_BESLUTTER", "Til beslutter"),
    RETURNERT_FRA_BESLUTTER("RETURNERT_FRA_BESLUTTER", "Returnert fra beslutter"),
    PAPIRSØKNAD("PAPIRSOKNAD", "Registrer papirsøknad"),
    UTBETALING_TIL_BRUKER("UTBETALING_TIL_BRUKER", "Utbetaling til bruker"),
    UTLANDSSAK("UTLANDSSAK", "Bosatt utland"),
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
    SAMMENSATT_KONTROLL("SAMMENSATT_KONTROLL", "Sammensatt kontroll"),
    ARBEID_INNTEKT("ARBEID_INNTEKT", "Arbeid og inntekt"),
    DØD("DØD", "Død eller dødfødsel"),
    NÆRING("NÆRING", "Selvstendig næringsdrivende"),
    PRAKSIS_UTSETTELSE("PRAKSIS_UTSETTELSE", "Praksis utsettelse"),
    BARE_FAR_RETT("BARE_FAR_RETT", "Bare far har rett"),
    MOR_UKJENT_UTLAND("MOR_UKJENT_UTLAND", "Gruppe 2"),
    REVURDERING_INNTEKTSMELDING("REVURDERING_INNTEKTSMELDING", "Revurdering inntektsmelding"),
    TERMINBEKREFTELSE("TERMINBEKREFTELSE", "Terminbekreftelse"),
    NYTT_VEDTAK("NYTT_VEDTAK", "Ny stønadsperiode"),
    UTSATT_START("UTSATT_START", "Utsatt start"),
    IKKE_VARSLET("IKKE_VARSLET", "Ikke varslet"),
    OVER_FIRE_RETTSGEBYR("OVER_FIRE_RETTSGEBYR", "Over 4 rettsgebyr"),
    HASTER("HASTER", "Haster")
    ;

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
