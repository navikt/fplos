package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;

import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavehendelseHåndterer.*;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.OppgaveUtil.oppgaveFra;

// håndterer hendelser hvor enhet avviker fra enhet tilknyttet åpen oppgave
@ApplicationScoped
public class EndreEnhetTransisjon implements FptilbakeOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(EndreEnhetTransisjon.class);

    private KøStatistikkTjeneste køStatistikkTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;

    public EndreEnhetTransisjon() {
    }

    public EndreEnhetTransisjon(KøStatistikkTjeneste køStatistikkTjeneste,
                                OppgaveTjeneste oppgaveTjeneste) {
        this.køStatistikkTjeneste = køStatistikkTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPGAVE_TIL_NY_ENHET;
    }

    @Override
    public void håndter(FptilbakeData data) {
        avsluttOppgave(data);
        opprettOppgave(data);
    }

    private void opprettOppgave(FptilbakeData data) {
        var aksjonspunkter = data.hendelse().getAksjonspunkter();
        var ansvarligSaksbehandler = data.hendelse().getAnsvarligSaksbehandler();
        var egenskapFinner = new FptilbakeOppgaveEgenskapFinner(aksjonspunkter, ansvarligSaksbehandler);
        var oppgave = nyOppgave(data);
        køStatistikkTjeneste.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.OPPRETTET)
                .behandlendeEnhet(oppgave.getBehandlendeEnhet())
                .behandlingId(oppgave.getBehandlingId());
        if (egenskapFinner.getAndreKriterier().contains(AndreKriterierType.TIL_BESLUTTER)) {
            oel.andreKriterierType(AndreKriterierType.TIL_BESLUTTER);
        }
        oppgaveTjeneste.lagre(oel.build());
        LOG.info("TBK: flytter oppgave til ny enhet for behandlingId {}.", oppgave.getBehandlingId());
    }

    private TilbakekrevingOppgave nyOppgave(FptilbakeData data) {
        var nyOppgave = oppgaveFra(data.hendelse());
        nyOppgave.setOppgaveEgenskaper(data.egenskapFinner());
        oppgaveTjeneste.lagre(nyOppgave);
        return nyOppgave;
    }

    private void avsluttOppgave(FptilbakeData data) {
        var hendelse = data.hendelse();
        var behandlingId = hendelse.getBehandlingId();
        var oppgave = oppgaveTjeneste.hentAktivOppgave(behandlingId).orElseThrow();
        køStatistikkTjeneste.lagre(oppgave, KøOppgaveHendelse.UT_TIL_ANNEN_KØ);
        oppgaveTjeneste.avsluttOppgaveMedEventLogg(oppgave, OppgaveEventType.LUKKET, ReservasjonKonstanter.NY_ENHET);
    }

}
