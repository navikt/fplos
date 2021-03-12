package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;

public class GjenåpneOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(GjenåpneOppgaveHendelseHåndterer.class);

    private final BehandlingFpsak behandlingFpsak;
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private final OppgaveStatistikk oppgaveStatistikk;

    public GjenåpneOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                           OppgaveStatistikk oppgaveStatistikk,
                                           BehandlingFpsak behandlingFpsak) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveStatistikk = oppgaveStatistikk;
        this.behandlingFpsak = behandlingFpsak;
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
        oppgaveStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
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
            gjenåpnetOppgave.getReservasjon().frigiReservasjon("Flyttet til ny enhet");
        }
    }

    private void oppdaterOppgave(Oppgave oppgave) {
        Oppgave tmp = oppgave(behandlingFpsak);
        oppgave.avstemMed(tmp);
        oppgaveRepository.lagre(oppgave);
    }

}
