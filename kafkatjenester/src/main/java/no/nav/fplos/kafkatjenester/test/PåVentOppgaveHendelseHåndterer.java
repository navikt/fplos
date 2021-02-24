package no.nav.fplos.kafkatjenester.test;

import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.KøOppgaveHendelse;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.oppgavestatistikk.OppgaveStatistikk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

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
        // TODO: innføre egen ventestatus der det ikke finnes oppgave fra før. Vurder å lagre oppgaveId i OppgaveEventLogg.
        var behandlingId = behandlingFpsak.getBehandlingId();
        var behandlendeEnhet = behandlingFpsak.getBehandlendeEnhetNavn();
        var aksjonspunkter = behandlingFpsak.getAksjonspunkter();
        var type = manueltSattPåVent(aksjonspunkter) ? OppgaveEventType.MANU_VENT : OppgaveEventType.VENT;
        var aksjonspunktFrist = aksjonspunktFrist(aksjonspunkter, type);
        LOG.info("Behandling er satt på vent, type {}", type);
        oppgaveStatistikk.lagre(behandlingId, KøOppgaveHendelse.OPPGAVE_SATT_PÅ_VENT);
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
        var oel = new OppgaveEventLogg(behandlingId, type, null, behandlendeEnhet, aksjonspunktFrist);
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
