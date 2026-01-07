package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;

public record Behandling(UUID behandlingUuid,
                         Saksnummer saksnummer,
                         FagsakYtelseType ytelse,
                         AktørId aktørId,
                         BehandlingType behandlingstype,
                         Behandlingsstatus behandlingsstatus,
                         LocalDateTime opprettetTidspunkt,
                         String behandlendeEnhetId,
                         LocalDate behandlingsfrist,
                         String ansvarligSaksbehandlerIdent,
                         List<Aksjonspunkt> aksjonspunkt,
                         List<Behandlingsårsak> behandlingsårsaker,
                         boolean faresignaler,
                         boolean refusjonskrav,
                         List<Saksegenskap> saksegenskaper,
                         LocalDate førsteUttaksdatoForeldrepenger, //null hvis ES og SVP
                         List<Behandlingsegenskap> behandlingsegenskaper,
                         Tilbakekreving tilbakekreving) {

    public enum Behandlingsstatus {
        OPPRETTET,
        UTREDES,
        FATTER_VEDTAK,
        IVERKSETTER_VEDTAK,
        AVSLUTTET,
    }

    public enum Saksegenskap {
        EØS_BOSATT_NORGE,
        BOSATT_UTLAND,
        SAMMENSATT_KONTROLL,
        DØD,
        NÆRING,
        BARE_FAR_RETT,
        PRAKSIS_UTSETTELSE,
        HASTER,
    }

    public enum Behandlingsegenskap {
        SYKDOMSVURDERING,
        MOR_UKJENT_UTLAND,
        FARESIGNALER,
        DIREKTE_UTBETALING,
        TILBAKEKREVING_SENDT_VARSEL,
        TILBAKEKREVING_OVER_FIRE_RETTSGEBYR
    }

    public record Tilbakekreving(BigDecimal feilutbetaltBeløp, LocalDate førsteFeilutbetalingDato) {
    }

    public record Aksjonspunkt(AksjonspunktType type, Aksjonspunktstatus status, LocalDateTime fristTidt) {
    }

    public enum Behandlingsårsak {
        SØKNAD,
        INNTEKTSMELDING,
        FOLKEREGISTER,
        PLEIEPENGER,
        ETTERKONTROLL,
        MANUELL,
        BERØRT,
        UTSATT_START,
        OPPHØR_NY_SAK,
        REGULERING,
        KLAGE_OMGJØRING,
        KLAGE_TILBAKEBETALING,
        ANNET
    }

    public enum AksjonspunktType {
        TIL_BESLUTTER,
        ANNET,
        KONTROLLER_TERMINBEKREFTELSE,
        AUTOMATISK_MARKERING_SOM_UTLAND,
        ARBEID_OG_INNTEKT,
        VURDER_FORMKRAV,
        PÅ_VENT,
        VURDER_NÆRING,
        PAPIRSØKNAD
    }
}
