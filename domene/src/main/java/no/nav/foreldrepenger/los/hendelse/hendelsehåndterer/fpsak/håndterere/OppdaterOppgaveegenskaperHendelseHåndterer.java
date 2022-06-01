package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;
import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.NY_ENHET;

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
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.OppgaveknytningerFørEtterOppdatering;

public class OppdaterOppgaveegenskaperHendelseHåndterer implements FpsakHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(OppdaterOppgaveegenskaperHendelseHåndterer.class);

    private final BehandlingFpsak behandlingFpsak;
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private final KøStatistikkTjeneste køStatistikk;
    private final OppgaveknytningerFørEtterOppdatering oppgaveknytningerFørEtterOppdatering = new OppgaveknytningerFørEtterOppdatering();

    public OppdaterOppgaveegenskaperHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                                      OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                      KøStatistikkTjeneste køStatistikk,
                                                      BehandlingFpsak behandlingFpsak) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.køStatistikk = køStatistikk;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    public void håndter() {
        var behandlingId = behandlingFpsak.getBehandlingId();
        var oppgave = oppgaveRepository.hentOppgaver(behandlingId).stream().filter(Oppgave::getAktiv).findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Finner ikke oppgave for oppdatering, behandlingId %s", behandlingId)));
        oppgaveknytningerFørEtterOppdatering.setKnytningerFørOppdatering(køStatistikk.hentOppgaveFiltreringKnytningerForOppgave(oppgave));
        oppdaterReservasjon(oppgave);
        oppdaterOppgave(oppgave);
        oppdaterOppgaveEgenskaper(oppgave);
        oppdaterOppgaveEventLogg();
        oppgaveknytningerFørEtterOppdatering.setKnytningerEtterOppdatering(køStatistikk.hentOppgaveFiltreringKnytningerForOppgave(oppgave));
        køStatistikk.lagre(oppgaveknytningerFørEtterOppdatering);
        LOG.info("Oppdater {} oppgaveId {}", SYSTEM, oppgave.getId());
    }

    private void oppdaterOppgaveEgenskaper(Oppgave gjenåpnetOppgave) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
    }

    private void oppdaterOppgaveEventLogg() {
        // TODO: innfør OppgaveEventType for å skille mellom oppdatering av egenskaper og gjenåpning. Ev dropp å logge oppdateringer?
        var oel = OppgaveEventLogg.builder()
                .behandlingId(behandlingFpsak.getBehandlingId())
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.GJENAPNET)
                .build();
        oppgaveRepository.lagre(oel);
    }

    private void oppdaterReservasjon(Oppgave oppgave) {
        if (!oppgave.getBehandlendeEnhet().equals(behandlingFpsak.getBehandlendeEnhetId())
                && oppgave.harAktivReservasjon()) {
            LOG.info("OppgaveId {} flyttes til ny enhet. Fjerner aktiv reservasjon.", oppgave.getId());
            oppgave.getReservasjon().frigiReservasjon(NY_ENHET);
        } else if (oppgave.harAktivReservasjon()) {
            var reservasjon = oppgave.getReservasjon();
            var nyReservertTil = reservasjon.getReservertTil().plusHours(2);
            LOG.info("Forlenger reservasjonId {} med to timer til {}", reservasjon.getId(), nyReservertTil);
            reservasjon.setReservertTil(nyReservertTil);
            oppgaveRepository.lagre(reservasjon);
        }
    }

    private void oppdaterOppgave(Oppgave oppgave) {
        var tmp = oppgave(behandlingFpsak);
        oppgave.avstemMed(tmp);
        oppgaveRepository.lagre(oppgave);
    }


}
