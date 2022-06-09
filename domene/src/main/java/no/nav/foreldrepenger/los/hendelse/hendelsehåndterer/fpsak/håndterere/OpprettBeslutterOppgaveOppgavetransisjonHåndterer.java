package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;

@ApplicationScoped
public class OpprettBeslutterOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(OpprettBeslutterOppgaveOppgavetransisjonHåndterer.class);
    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkTjeneste køStatistikk;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;


    @Inject
    public OpprettBeslutterOppgaveOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                             OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                             KøStatistikkTjeneste køStatistikk) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.køStatistikk = køStatistikk;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
    }

    public OpprettBeslutterOppgaveOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingFpsak behandlingFpsak) {
        håndterEksisterendeOppgave(behandlingFpsak.getBehandlingId(), behandlingFpsak);
        var oppgave = opprettOppgave(behandlingFpsak);
        opprettOppgaveEgenskaper(oppgave, behandlingFpsak);
        opprettOppgaveEventLogg(oppgave, behandlingFpsak);
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPRETT_BESLUTTEROPPGAVE;
    }

    private Oppgave opprettOppgave(BehandlingFpsak behandlingFpsak) {
        var oppgave = oppgave(behandlingFpsak);
        oppgaveTjeneste.lagre(oppgave);
        return oppgave;
    }

    private void håndterEksisterendeOppgave(BehandlingId behandlingId, BehandlingFpsak behandlingFpsak) {
        oppgaveTjeneste.hentAktivOppgave(behandlingId)
                .stream().peek(o -> LOG.trace("HåndterEksisterendeOppgave, peek på oppgave {}", o))
                .findFirst()
                .filter(Oppgave::getAktiv)
                .ifPresentOrElse(sbo -> {
                    køStatistikk.lagre(sbo, KøOppgaveHendelse.LUKKET_OPPGAVE);
                    oppgaveTjeneste.avsluttOppgaveMedEventLogg(sbo, ReservasjonKonstanter.OPPGAVE_AVSLUTTET);
                    LOG.info("Avslutter saksbehandler1 oppgave");
                }, () -> LOG.info("Fant ingen aktiv saksbehandler1-oppgave"));
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave, BehandlingFpsak behandlingFpsak) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

    private void opprettOppgaveEventLogg(Oppgave oppgave, BehandlingFpsak behandlingFpsak) {
        LOG.info("Oppretter {} oppgave til beslutter", SYSTEM);
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.OPPRETTET)
                .behandlingId(behandlingFpsak.getBehandlingId())
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .andreKriterierType(AndreKriterierType.TIL_BESLUTTER)
                .build();
        oppgaveTjeneste.lagre(oel);
    }

}
