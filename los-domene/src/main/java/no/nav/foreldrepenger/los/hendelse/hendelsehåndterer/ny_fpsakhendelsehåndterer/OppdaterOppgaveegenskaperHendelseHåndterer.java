package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveknytningerFørEtterOppdatering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.OppgaveUtil.oppgave;

public class OppdaterOppgaveegenskaperHendelseHåndterer implements FpsakHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(OppdaterOppgaveegenskaperHendelseHåndterer.class);

    private final BehandlingFpsak behandlingFpsak;
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private final OppgaveStatistikk oppgaveStatistikk;
    private final OppgaveknytningerFørEtterOppdatering oppgaveknytningerFørEtterOppdatering = new OppgaveknytningerFørEtterOppdatering();

    public OppdaterOppgaveegenskaperHendelseHåndterer(OppgaveRepository oppgaveRepository,
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
        var oppgave = oppgaveRepository.hentOppgaver(behandlingId).stream().filter(Oppgave::getAktiv).findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Finner ikke oppgave for oppdatering, behandlingId %s", behandlingId)));

        oppgaveknytningerFørEtterOppdatering.setKnytningerFørOppdatering(oppgaveStatistikk.hentOppgaveFiltreringKnytningerForOppgave(oppgave));

        oppdaterReservasjon(oppgave);
        oppdaterOppgave(oppgave);
        oppdaterOppgaveEgenskaper(oppgave);
        oppdaterOppgaveEventLogg();

        oppgaveknytningerFørEtterOppdatering.setKnytningerEtterOppdatering(oppgaveStatistikk.hentOppgaveFiltreringKnytningerForOppgave(oppgave));
        oppgaveStatistikk.lagre(oppgaveknytningerFørEtterOppdatering);

        LOG.info("Oppdater {} oppgaveId {}", system, oppgave.getId());
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

    private void oppdaterReservasjon(Oppgave gjenåpnetOppgave) {
        if (!gjenåpnetOppgave.getBehandlendeEnhet().equals(behandlingFpsak.getBehandlendeEnhetId())
                && gjenåpnetOppgave.harAktivReservasjon()) {
            LOG.info("OppgaveId {} flyttes til ny enhet. Fjerner aktiv reservasjon.", gjenåpnetOppgave.getId());
            gjenåpnetOppgave.getReservasjon().frigiReservasjon("Flyttet til ny enhet");
        } else if (gjenåpnetOppgave.harAktivReservasjon()) {
            var reservasjon = gjenåpnetOppgave.getReservasjon();
            var nyReservertTil = reservasjon.getReservertTil().plusHours(2);
            LOG.info("Forlenger reservasjonId {} med to timer til {}", reservasjon.getId(), nyReservertTil);
            reservasjon.endreReservasjonPåOppgave(nyReservertTil);
            oppgaveRepository.lagre(reservasjon);
        }
    }

    private void oppdaterOppgave(Oppgave oppgave) {
        Oppgave tmp = oppgave(behandlingFpsak);
        oppgave.avstemMed(tmp);
        oppgaveRepository.lagre(oppgave);
    }


}
