package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class ReturFraBeslutterOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(ReturFraBeslutterOppgavetransisjonHåndterer.class);
    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;

    @Inject
    public ReturFraBeslutterOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                       OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                       ReservasjonTjeneste reservasjonTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
    }

    public ReturFraBeslutterOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk) {
        avsluttBeslutterOppgave(behandlingId, behandling.behandlendeEnhetId());
        var saksbehandlerOppgave = OppgaveUtil.oppgave(behandlingId, behandling);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(saksbehandlerOppgave, new FpsakOppgaveEgenskapFinner(behandling));
        oppgaveTjeneste.lagre(saksbehandlerOppgave);
        if (behandling.ansvarligSaksbehandlerIdent() != null) {
            reservasjonTjeneste.opprettReservasjonOgLagre(saksbehandlerOppgave, behandling.ansvarligSaksbehandlerIdent(), ReservasjonKonstanter.RETUR_FRA_BESLUTTER);
        }
        LOG.info("Retur fra beslutter, oppretter oppgave og flytter reservasjon til ansvarlig saksbehandler");
        opprettOppgaveEventLogg(saksbehandlerOppgave, behandling.behandlendeEnhetId());
    }


    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.RETUR_FRA_BESLUTTER_OPPGAVE;
    }

    private void avsluttBeslutterOppgave(BehandlingId behandlingId, String enhet) {
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
        var oel = OppgaveEventLogg.builder().behandlingId(behandlingId).behandlendeEnhet(enhet).type(OppgaveEventType.LUKKET).build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Avslutter {} beslutteroppgave", SYSTEM);
    }

    private void opprettOppgaveEventLogg(Oppgave oppgave, String enhet) {
        var oel = OppgaveEventLogg.builder().behandlingId(oppgave.getBehandlingId()).behandlendeEnhet(enhet).type(OppgaveEventType.OPPRETTET).build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Retur fra beslutter, oppretter {} saksbehandler-oppgave med oppgaveId {}", SYSTEM, oppgave.getId());
    }

}
