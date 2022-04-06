package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import static no.nav.foreldrepenger.los.felles.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

public class PåVentOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(PåVentOppgaveHendelseHåndterer.class);
    private final OppgaveTjeneste oppgaveTjeneste;
    private final KøStatistikkTjeneste køStatistikk;
    private final BehandlingFpsak behandlingFpsak;

    public PåVentOppgaveHendelseHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                          KøStatistikkTjeneste køStatistikk,
                                          BehandlingFpsak behandling) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.køStatistikk = køStatistikk;
        this.behandlingFpsak = behandling;
    }

    @Override
    public void håndter() {
        var behandlingId = behandlingFpsak.getBehandlingId();
        var behandlendeEnhet = behandlingFpsak.getBehandlendeEnhetId();
        var aksjonspunkter = behandlingFpsak.getAksjonspunkter();
        var venteType = manueltSattPåVent(aksjonspunkter) ? OppgaveEventType.MANU_VENT : OppgaveEventType.VENT;
        var aksjonspunktFrist = aksjonspunktFrist(aksjonspunkter, venteType);
        oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingId)
                .filter(Oppgave::getAktiv)
                .ifPresentOrElse(o -> {
                            LOG.info("{} behandling er satt på vent, type {}. Lukker oppgave.", SYSTEM, venteType);
                            køStatistikk.lagre(behandlingId, KøOppgaveHendelse.OPPGAVE_SATT_PÅ_VENT);
                            oppgaveTjeneste.avsluttOppgaveUtenEventLogg(behandlingId);
                        },
                        () -> LOG.info("{} behandling er satt på vent, type {}", SYSTEM, venteType));
        var oel = new OppgaveEventLogg(behandlingId, venteType, null, behandlendeEnhet, aksjonspunktFrist);
        oppgaveTjeneste.lagre(oel);
    }

    private static LocalDateTime aksjonspunktFrist(List<Aksjonspunkt> aksjonspunkter, OppgaveEventType type) {
        if (type.equals(OppgaveEventType.MANU_VENT)) {
            return finnVentefrist(aksjonspunkter, Aksjonspunkt::erManueltPåVent);
        }
        return finnVentefrist(aksjonspunkter, Aksjonspunkt::erPåVent);
    }

    private static LocalDateTime finnVentefrist(List<Aksjonspunkt> aksjonspunkter, Predicate<Aksjonspunkt> predicate) {
        return safeStream(aksjonspunkter).filter(Aksjonspunkt::erAktiv)
                .filter(predicate)
                .map(Aksjonspunkt::getFristTid)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    private static boolean manueltSattPåVent(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream().filter(Aksjonspunkt::erAktiv).anyMatch(Aksjonspunkt::erManueltPåVent);
    }
}
