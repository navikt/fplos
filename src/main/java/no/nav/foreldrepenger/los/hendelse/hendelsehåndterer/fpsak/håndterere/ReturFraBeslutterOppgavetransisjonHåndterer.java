package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class ReturFraBeslutterOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(ReturFraBeslutterOppgavetransisjonHåndterer.class);
    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkTjeneste køStatistikk;
    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;

    @Inject
    public ReturFraBeslutterOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                       OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                       ReservasjonTjeneste reservasjonTjeneste,
                                                       KøStatistikkTjeneste køStatistikk) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.køStatistikk = køStatistikk;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
    }

    public ReturFraBeslutterOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk) {
        håndterEksisterendeOppgave(behandlingId, behandling.behandlendeEnhetId());
        var oppgave = opprettOppgave(behandlingId, behandling);
        opprettOppgaveEgenskaper(oppgave, behandling);
        opprettOppgaveEventLogg(oppgave, behandling.behandlendeEnhetId());
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }


    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.RETUR_FRA_BESLUTTER_OPPGAVE;
    }

    private void håndterEksisterendeOppgave(BehandlingId behandlingId, String enhet) {
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
        var oel = OppgaveEventLogg.builder().behandlingId(behandlingId).behandlendeEnhet(enhet).type(OppgaveEventType.LUKKET).build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Avslutter {} beslutteroppgave", SYSTEM);
    }

    private Oppgave opprettOppgave(BehandlingId behandlingId, LosBehandlingDto behandlingFpsak) {
        var oppgave = OppgaveUtil.oppgave(behandlingId, behandlingFpsak);
        oppgaveTjeneste.lagre(oppgave);
        reservasjonTjeneste.opprettReservasjon(oppgave, behandlingFpsak.ansvarligSaksbehandlerIdent(), "Retur fra beslutter");
        LOG.info("Retur fra beslutter, oppretter oppgave og flytter reservasjon til ansvarlig saksbehandler");
        return oppgave;
    }

    private void opprettOppgaveEventLogg(Oppgave oppgave, String enhet) {
        var oel = OppgaveEventLogg.builder().behandlingId(oppgave.getBehandlingId()).behandlendeEnhet(enhet).type(OppgaveEventType.OPPRETTET).build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Retur fra beslutter, oppretter {} saksbehandler-oppgave med oppgaveId {}", SYSTEM, oppgave.getId());
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave, LosBehandlingDto behandlingFpsak) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

}
