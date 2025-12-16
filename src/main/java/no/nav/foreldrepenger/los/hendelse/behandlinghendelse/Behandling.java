package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;

public record Behandling(UUID behandlingUuid,
                         String saksnummer,
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
                         Foreldrepenger foreldrepenger,
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

    }

    public enum Behandlingsegenskap {

    }

    public record Foreldrepenger(LocalDate førsteUttakDato) {
    }

    public record Tilbakekreving(BigDecimal feilutbetaltBeløp, LocalDate førsteFeilutbetalingDato) {
    }

    public record Aksjonspunkt(AksjonspunktDefinisjon definisjon, Aksjonspunktstatus status, LocalDateTime fristTidt {
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

    public enum AksjonspunktDefinisjon {
    }
}
