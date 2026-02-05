package no.nav.foreldrepenger.los.oppgave;

import static java.util.EnumSet.allOf;
import static java.util.Set.of;
import static no.nav.foreldrepenger.los.oppgave.BehandlingType.FØRSTEGANGSSØKNAD;
import static no.nav.foreldrepenger.los.oppgave.BehandlingType.KLAGE;
import static no.nav.foreldrepenger.los.oppgave.BehandlingType.REVURDERING;
import static no.nav.foreldrepenger.los.oppgave.BehandlingType.TILBAKEBETALING;
import static no.nav.foreldrepenger.los.oppgave.BehandlingType.TILBAKEBETALING_REVURDERING;
import static no.nav.foreldrepenger.los.oppgave.FagsakYtelseType.ENGANGSTØNAD;
import static no.nav.foreldrepenger.los.oppgave.FagsakYtelseType.FORELDREPENGER;
import static no.nav.foreldrepenger.los.oppgave.FagsakYtelseType.SVANGERSKAPSPENGER;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;
import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum AndreKriterierType implements Kodeverdi {

    VURDER_SYKDOM("VURDER_SYKDOM", "Vurder sykdom", false, of(FORELDREPENGER), of(FØRSTEGANGSSØKNAD, REVURDERING)),
    PLEIEPENGER("PLEIEPENGER", "Pleiepenger", false, of(FORELDREPENGER), of(REVURDERING)),
    BARE_FAR_RETT("BARE_FAR_RETT", "Bare far har rett", false, of(FORELDREPENGER), of(FØRSTEGANGSSØKNAD, REVURDERING)),
    MOR_UKJENT_UTLAND("MOR_UKJENT_UTLAND", "Gruppe 2", true, of(FORELDREPENGER), of(FØRSTEGANGSSØKNAD, REVURDERING)),
    UTSATT_START("UTSATT_START", "Utsatt start", false, of(FORELDREPENGER), of(REVURDERING)),
    PRAKSIS_UTSETTELSE("PRAKSIS_UTSETTELSE", "Praksis utsettelse", false, of(FORELDREPENGER), of(FØRSTEGANGSSØKNAD, REVURDERING)),
    BERØRT_BEHANDLING("BERØRT_BEHANDLING", "Berørt behandling", false, of(FORELDREPENGER), of(REVURDERING)),
    ENDRINGSSØKNAD("ENDRINGSSOKNAD", "Endringssøknad", false, of(FORELDREPENGER), of(REVURDERING)),

    PAPIRSØKNAD("PAPIRSOKNAD", "Registrer papirsøknad", true, alleYtelseTyper(), of(FØRSTEGANGSSØKNAD, REVURDERING)),
    UTBETALING_TIL_BRUKER("UTBETALING_TIL_BRUKER", "Utbetaling til bruker", false, of(FORELDREPENGER, SVANGERSKAPSPENGER), of(FØRSTEGANGSSØKNAD, REVURDERING)),
    VURDER_FARESIGNALER("VURDER_FARESIGNALER", "Vurder faresignaler", false, alleYtelseTyper(), of(FØRSTEGANGSSØKNAD)),
    VURDER_EØS_OPPTJENING("VURDER_EØS_OPPTJENING", "Vurder behov for SED", false, of(FORELDREPENGER, SVANGERSKAPSPENGER), of(FØRSTEGANGSSØKNAD)),
    ARBEID_INNTEKT("ARBEID_INNTEKT", "Arbeid og inntekt", false, of(FORELDREPENGER, SVANGERSKAPSPENGER), of(FØRSTEGANGSSØKNAD, REVURDERING)),
    NÆRING("NÆRING", "Selvstendig næringsdrivende", false, of(FORELDREPENGER, SVANGERSKAPSPENGER)),
    REVURDERING_INNTEKTSMELDING("REVURDERING_INNTEKTSMELDING", "Revurdering inntektsmelding", false, of(FORELDREPENGER, SVANGERSKAPSPENGER), of(REVURDERING)),
    TERMINBEKREFTELSE("TERMINBEKREFTELSE", "Terminbekreftelse", false, of(ENGANGSTØNAD, FORELDREPENGER), of(FØRSTEGANGSSØKNAD, REVURDERING)),
    NYTT_VEDTAK("NYTT_VEDTAK", "Ny stønadsperiode", false, of(FORELDREPENGER, SVANGERSKAPSPENGER), of(FØRSTEGANGSSØKNAD, REVURDERING)),

    KLAGE_PÅ_TILBAKEBETALING("KLAGE_PÅ_TILBAKEBETALING", "Klage på tilbakebetaling", false, alleYtelseTyper(), of(KLAGE)),
    VURDER_FORMKRAV("VURDER_FORMKRAV", "Vurder formkrav", false, alleYtelseTyper(), of(KLAGE)),

    IKKE_VARSLET("IKKE_VARSLET", "Ikke varslet", false, alleYtelseTyper(), of(TILBAKEBETALING, TILBAKEBETALING_REVURDERING)),
    OVER_FIRE_RETTSGEBYR("OVER_FIRE_RETTSGEBYR", "Over 4 rettsgebyr", false, alleYtelseTyper(), of(TILBAKEBETALING, TILBAKEBETALING_REVURDERING)),

    TIL_BESLUTTER("TIL_BESLUTTER", "Til beslutter", true),
    SAMMENSATT_KONTROLL("SAMMENSATT_KONTROLL", "Sammensatt kontroll", true),
    UTLANDSSAK("UTLANDSSAK", "Bosatt utland", true),
    EØS_SAK("EØS_SAK", "EØS (bosatt Norge)", true),
    KODE7_SAK("KODE7_SAK", "Kode 7", true),
    RETURNERT_FRA_BESLUTTER("RETURNERT_FRA_BESLUTTER", "Returnert fra beslutter"),
    DØD("DØD", "Død eller dødfødsel"),
    HASTER("HASTER", "Haster")
    ;

    @JsonValue
    @EnumeratedValue
    private final String kode;
    @JsonIgnore
    private final String navn;

    private final Set<FagsakYtelseType> valgbarForYtelseTyper;
    private final Set<BehandlingType> valgbarForBehandlingTyper;
    private final boolean defaultEkskludert;

    AndreKriterierType(String kode,
                       String navn,
                       boolean defaultEkskludert,
                       Set<FagsakYtelseType> valgbarForYtelseTyper,
                       Set<BehandlingType> valgbarForBehandlingTyper) {
        this.kode = kode;
        this.navn = navn;
        this.valgbarForYtelseTyper = valgbarForYtelseTyper;
        this.valgbarForBehandlingTyper = valgbarForBehandlingTyper;
        this.defaultEkskludert = defaultEkskludert;
    }

    AndreKriterierType(String kode, String navn, boolean defaultEkskludert, Set<FagsakYtelseType> valgbarForYtelseTyper) {
        this(kode, navn, defaultEkskludert, valgbarForYtelseTyper, allOf(BehandlingType.class));
    }

    AndreKriterierType(String kode, String navn, boolean defaultEkskludert) {
        this(kode, navn, defaultEkskludert, alleYtelseTyper(), allOf(BehandlingType.class));
    }

    AndreKriterierType(String kode, String navn) {
        this(kode, navn, false);
    }

    private static Set<FagsakYtelseType> alleYtelseTyper() {
        return allOf(FagsakYtelseType.class);
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }

    public Set<FagsakYtelseType> getValgbarForYtelseTyper() {
        return valgbarForYtelseTyper;
    }

    public Set<BehandlingType> getValgbarForBehandlingTyper() {
        return valgbarForBehandlingTyper;
    }

    public boolean isDefaultEkskludert() {
        return defaultEkskludert;
    }

    public boolean erTilBeslutter() {
        return this.equals(TIL_BESLUTTER);
    }
}
