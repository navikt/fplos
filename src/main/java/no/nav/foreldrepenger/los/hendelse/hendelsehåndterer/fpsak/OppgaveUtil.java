package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.util.Optional;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public class OppgaveUtil {

    private OppgaveUtil() {
    }

    public static Oppgave oppgave(BehandlingId behandlingId, LosBehandlingDto behandlingFpsak) {
        return Oppgave.builder()
            .medSystem(Fagsystem.FPSAK.name())
            .medSaksnummer(new Saksnummer(behandlingFpsak.saksnummer()))
            .medAktørId(new AktørId(behandlingFpsak.aktørId().getAktørId()))
            .medBehandlendeEnhet(behandlingFpsak.behandlendeEnhetId())
            .medBehandlingType(mapBehandlingstype(behandlingFpsak.behandlingstype()))
            .medFagsakYtelseType(mapYtelse(behandlingFpsak.ytelse()))
            .medAktiv(true)
            .medBehandlingOpprettet(behandlingFpsak.opprettetTidspunkt())
            .medUtfortFraAdmin(false)
            .medBehandlingStatus(mapBehandlingsstatus(behandlingFpsak.behandlingsstatus()))
            .medBehandlingId(behandlingId)
            .medFørsteStønadsdag(
                Optional.ofNullable(behandlingFpsak.foreldrepengerDto()).map(LosBehandlingDto.LosForeldrepengerDto::førsteUttakDato).orElse(null))
            .medBehandlingsfrist(behandlingFpsak.behandlingsfrist() != null ? behandlingFpsak.behandlingsfrist().atStartOfDay() : null)
            .build();
    }

    public static BehandlingType mapBehandlingstype(Behandlingstype behandlingstype) {
        return switch (behandlingstype) {
            case FØRSTEGANGS -> BehandlingType.FØRSTEGANGSSØKNAD;
            case REVURDERING -> BehandlingType.REVURDERING;
            case TILBAKEBETALING -> BehandlingType.TILBAKEBETALING;
            case TILBAKEBETALING_REVURDERING -> BehandlingType.TILBAKEBETALING_REVURDERING;
            case KLAGE -> BehandlingType.KLAGE;
            case ANKE -> BehandlingType.ANKE;
            case INNSYN -> BehandlingType.INNSYN;
        };
    }

    public static BehandlingStatus mapBehandlingsstatus(Behandlingsstatus behandlingsstatus) {
        return switch (behandlingsstatus) {
            case OPPRETTET -> BehandlingStatus.OPPRETTET;
            case UTREDES -> BehandlingStatus.UTREDES;
            case FATTER_VEDTAK -> BehandlingStatus.FATTER_VEDTAK;
            case IVERKSETTER_VEDTAK -> BehandlingStatus.IVERKSETTER_VEDTAK;
            case AVSLUTTET -> BehandlingStatus.AVSLUTTET;
        };
    }

    public static FagsakYtelseType mapYtelse(Ytelse ytelse) {
        return switch (ytelse) {
            case ENGANGSTØNAD -> FagsakYtelseType.ENGANGSTØNAD;
            case FORELDREPENGER -> FagsakYtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

}
