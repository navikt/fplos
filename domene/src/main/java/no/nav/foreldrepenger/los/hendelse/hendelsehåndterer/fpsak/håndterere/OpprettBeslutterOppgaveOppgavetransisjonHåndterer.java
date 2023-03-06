package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

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
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling) {
        håndterEksisterendeOppgave(behandlingId);
        var oppgave = opprettOppgave(behandlingId, behandling);
        opprettOppgaveEgenskaper(oppgave, behandling);
        opprettOppgaveEventLogg(behandlingId, behandling.behandlendeEnhetId());
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPRETT_BESLUTTEROPPGAVE;
    }

    private Oppgave opprettOppgave(BehandlingId behandlingId, LosBehandlingDto behandlingFpsak) {
        var oppgave = OppgaveUtil.oppgave(behandlingId, behandlingFpsak);
        oppgaveTjeneste.lagre(oppgave);
        return oppgave;
    }

    private void håndterEksisterendeOppgave(BehandlingId behandlingId) {
        oppgaveTjeneste.hentAktivOppgave(behandlingId)
            .stream()
            .peek(o -> LOG.trace("HåndterEksisterendeOppgave, peek på oppgave {}", o))
            .findFirst()
            .filter(Oppgave::getAktiv)
            .ifPresentOrElse(sbo -> {
                køStatistikk.lagre(sbo, KøOppgaveHendelse.LUKKET_OPPGAVE);
                oppgaveTjeneste.avsluttOppgaveMedEventLogg(sbo, OppgaveEventType.LUKKET, ReservasjonKonstanter.OPPGAVE_AVSLUTTET);
                LOG.info("Avslutter saksbehandler1 oppgave");
            }, () -> LOG.info("Fant ingen aktiv saksbehandler1-oppgave"));
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave, LosBehandlingDto behandlingFpsak) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

    private void opprettOppgaveEventLogg(BehandlingId behandlingId, String enhet) {
        LOG.info("Oppretter {} oppgave til beslutter", SYSTEM);
        var oel = OppgaveEventLogg.builder()
            .type(OppgaveEventType.OPPRETTET)
            .behandlingId(behandlingId)
            .behandlendeEnhet(enhet)
            .andreKriterierType(AndreKriterierType.TIL_BESLUTTER)
            .build();
        oppgaveTjeneste.lagre(oel);
    }

}
