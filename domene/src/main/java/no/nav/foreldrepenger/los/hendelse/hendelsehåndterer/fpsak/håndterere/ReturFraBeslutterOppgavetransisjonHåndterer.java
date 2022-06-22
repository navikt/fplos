package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;

@ApplicationScoped
public class ReturFraBeslutterOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(ReturFraBeslutterOppgavetransisjonHåndterer.class);
    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkTjeneste køStatistikk;
    private ReservasjonTjeneste reservasjonTjeneste;

    @Inject
    public ReturFraBeslutterOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                       ReservasjonTjeneste reservasjonTjeneste,
                                                       KøStatistikkTjeneste køStatistikk) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.køStatistikk = køStatistikk;
    }

    public ReturFraBeslutterOppgavetransisjonHåndterer() {
    }

    public void håndter(BehandlingFpsak behandlingFpsak) {
        håndterEksisterendeOppgave(behandlingFpsak);
        var oppgave = opprettOppgave(behandlingFpsak);
        opprettOppgaveEventLogg(oppgave, behandlingFpsak);
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.RETUR_FRA_BESLUTTER_OPPGAVE;
    }

    private void håndterEksisterendeOppgave(BehandlingFpsak behandlingFpsak) {
        var behandlingId = behandlingFpsak.getBehandlingId();
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
        var oel = OppgaveEventLogg.builder()
                .behandlingId(behandlingId)
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.LUKKET)
                .build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Avslutter {} beslutteroppgave", SYSTEM);
    }

    private Oppgave opprettOppgave(BehandlingFpsak behandlingFpsak) {
        var oppgave = oppgave(behandlingFpsak);
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgave.setOppgaveEgenskaper(egenskapFinner);
        oppgaveTjeneste.lagre(oppgave);
        reservasjonTjeneste.opprettReservasjon(oppgave, behandlingFpsak.getAnsvarligSaksbehandler(), "Retur fra beslutter");
        LOG.info("Retur fra beslutter, oppretter oppgave og flytter reservasjon til ansvarlig saksbehandler");
        return oppgave;
    }


    private void opprettOppgaveEventLogg(Oppgave oppgave, BehandlingFpsak behandlingFpsak) {
        var oel = OppgaveEventLogg.builder()
                .behandlingId(oppgave.getBehandlingId())
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.OPPRETTET)
                .build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Retur fra beslutter, oppretter {} saksbehandler-oppgave med oppgaveId {}", SYSTEM, oppgave.getId());
    }


}
