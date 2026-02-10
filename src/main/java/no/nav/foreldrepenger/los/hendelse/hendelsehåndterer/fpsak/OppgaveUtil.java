package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Ytelse;

public class OppgaveUtil {

    private OppgaveUtil() {
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

    public static FagsakYtelseType mapYtelse(Ytelse ytelse) {
        return switch (ytelse) {
            case ENGANGSTØNAD -> FagsakYtelseType.ENGANGSTØNAD;
            case FORELDREPENGER -> FagsakYtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

}
