package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;
import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.NY_ENHET;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

@SuppressWarnings("ClassCanBeRecord")
public class GjenåpneOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(GjenåpneOppgaveHendelseHåndterer.class);

    private final BehandlingFpsak behandlingFpsak;
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private final KøStatistikkTjeneste køStatistikk;
    private final ReservasjonTjeneste reservasjonTjeneste;

    public GjenåpneOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                            OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                            KøStatistikkTjeneste køStatistikk,
                                            BehandlingFpsak behandlingFpsak,
                                            ReservasjonTjeneste reservasjonTjeneste) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.køStatistikk = køStatistikk;
        this.behandlingFpsak = behandlingFpsak;
        this.reservasjonTjeneste = reservasjonTjeneste;
    }

    @Override
    public void håndter() {
        var behandlingId = behandlingFpsak.getBehandlingId();
        var oppgave = oppgaveRepository.gjenåpneOppgaveForBehandling(behandlingId)
                .orElseThrow(() -> new IllegalStateException(String.format("Finner ikke oppgave for gjenåpning, behandlingId %s", behandlingId)));
        håndterReservasjon(oppgave);
        oppdaterOppgave(oppgave);
        oppdaterOppgaveEgenskaper(oppgave);
        oppdaterOppgaveEventLogg();
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
        LOG.info("Gjenåpnet {} oppgaveId {}", SYSTEM, oppgave.getId());
    }

    private void oppdaterOppgaveEgenskaper(Oppgave gjenåpnetOppgave) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
    }

    private void oppdaterOppgaveEventLogg() {
        var oel = OppgaveEventLogg.builder()
                .behandlingId(behandlingFpsak.getBehandlingId())
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.GJENAPNET)
                .build();
        oppgaveRepository.lagre(oel);
    }

    private void håndterReservasjon(Oppgave gjenåpnetOppgave) {
        Optional.ofNullable(gjenåpnetOppgave.getReservasjon())
                .map(Reservasjon::getReservertTil)
                .ifPresent(reservertTil -> {
                            var nå = LocalDateTime.now();
                            var duration = Duration.between(reservertTil, nå);
                            if (reservertTil.isAfter(nå)) {
                                LOG.info("Gjenåpnet oppgave har aktiv reservasjon {} " +
                                        "minutter frem i tid", duration.abs().toMinutes());
                            } else {
                                LOG.info("Gjenåpnet oppgave er tilknyttet inaktiv reservasjon " +
                                        "lukket for {} minutter siden", duration.toMinutes());
                            }
                        }
                );
        if (!gjenåpnetOppgave.getBehandlendeEnhet().equals(behandlingFpsak.getBehandlendeEnhetId())
                && gjenåpnetOppgave.harAktivReservasjon()) {
            LOG.info("OppgaveId {} flyttes til ny enhet. Fjerner aktiv reservasjon.", gjenåpnetOppgave.getId());
            reservasjonTjeneste.slettReservasjonMedEventLogg(gjenåpnetOppgave.getReservasjon(), NY_ENHET);
        }
    }

    private void oppdaterOppgave(Oppgave oppgave) {
        var tmp = oppgave(behandlingFpsak);
        oppgave.avstemMed(tmp);
        oppgaveRepository.lagre(oppgave);
    }

}
