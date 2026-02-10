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

record OppgaveGrunnlag(UUID behandlingUuid,
                       Saksnummer saksnummer,
                       FagsakYtelseType ytelse,
                       AktørId aktørId,
                       BehandlingType behandlingstype,
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
                       BehandlingStatus behandlingStatus,
                       LocalDateTime feilutbetalingStart,
                       BigDecimal feilutbetalingBeløp) {

    enum Saksegenskap {
        EØS_BOSATT_NORGE,
        BOSATT_UTLAND,
        SAMMENSATT_KONTROLL,
        DØD,
        NÆRING,
        BARE_FAR_RETT,
        PRAKSIS_UTSETTELSE,
        HASTER,
    }

    enum Behandlingsegenskap {
        SYKDOMSVURDERING,
        MOR_UKJENT_UTLAND,
        FARESIGNALER,
        DIREKTE_UTBETALING,
        REFUSJONSKRAV,
        TILBAKEKREVING_SENDT_VARSEL,
        TILBAKEKREVING_OVER_FIRE_RETTSGEBYR
    }

    enum BehandlingStatus {
        OPPRETTET,
        UTREDES,
        FATTER_VEDTAK,
        IVERKSETTER_VEDTAK,
        AVSLUTTET,
    }

    record Aksjonspunkt(AksjonspunktType type, Aksjonspunktstatus status, LocalDateTime fristTid) {
    }

    enum Behandlingsårsak {
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

    enum AksjonspunktType {
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
