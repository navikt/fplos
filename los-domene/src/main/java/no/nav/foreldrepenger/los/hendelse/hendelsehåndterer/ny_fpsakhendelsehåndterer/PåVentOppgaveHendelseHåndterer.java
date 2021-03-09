package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static no.nav.foreldrepenger.los.felles.util.StreamUtil.safeStream;

public class PåVentOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(PåVentOppgaveHendelseHåndterer.class);
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveStatistikk oppgaveStatistikk;
    private final BehandlingFpsak behandlingFpsak;

    public PåVentOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository, OppgaveStatistikk oppgaveStatistikk, BehandlingFpsak behandling) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveStatistikk = oppgaveStatistikk;
        this.behandlingFpsak = behandling;
    }

    @Override
    public void håndter() {
        var behandlingId = behandlingFpsak.getBehandlingId();
        var behandlendeEnhet = behandlingFpsak.getBehandlendeEnhetId();
        var aksjonspunkter = behandlingFpsak.getAksjonspunkter();
        var venteType = manueltSattPåVent(aksjonspunkter) ? OppgaveEventType.MANU_VENT : OppgaveEventType.VENT;
        var aksjonspunktFrist = aksjonspunktFrist(aksjonspunkter, venteType);
        var finnesAktivOppgave = oppgaveRepository.hentOppgaver(behandlingId).stream().anyMatch(Oppgave::getAktiv);
        if (finnesAktivOppgave) {
            LOG.info("{} behandling er satt på vent, type {}. Lukker oppgave.", system, venteType);
            oppgaveStatistikk.lagre(behandlingId, KøOppgaveHendelse.OPPGAVE_SATT_PÅ_VENT);
            oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
        } else {
            LOG.info("{} behandling er satt på vent, type {}", system, venteType);
        }
        var oel = new OppgaveEventLogg(behandlingId, venteType, null, behandlendeEnhet, aksjonspunktFrist);
        oppgaveRepository.lagre(oel);
    }

    private static LocalDateTime aksjonspunktFrist(List<Aksjonspunkt> aksjonspunkter, OppgaveEventType type) {
        if (type.equals(OppgaveEventType.MANU_VENT)) {
            return finnVentefrist(aksjonspunkter, Aksjonspunkt::erManueltPåVent);
        }
        return finnVentefrist(aksjonspunkter, Aksjonspunkt::erPåVent);
    }

    private static LocalDateTime finnVentefrist(List<Aksjonspunkt> aksjonspunkter, Predicate<Aksjonspunkt> predicate) {
        return safeStream(aksjonspunkter)
                .filter(Aksjonspunkt::erAktiv)
                .filter(predicate)
                .map(Aksjonspunkt::getFristTid)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    private static boolean manueltSattPåVent(List<Aksjonspunkt> aksjonspunkt) {
        return aksjonspunkt.stream()
                .filter(Aksjonspunkt::erAktiv)
                .anyMatch(Aksjonspunkt::erManueltPåVent);
    }
}
