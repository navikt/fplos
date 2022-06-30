package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.OppgaveUtil.oppgaveFra;
import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.TIL_BESLUTTER;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavehendelseHåndterer.FptilbakeData;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

@ApplicationScoped
public class TilBeslutterOppgaveTransisjon implements FptilbakeOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(TilBeslutterOppgaveTransisjon.class);
    private KøStatistikkTjeneste køStatistikk;
    private OppgaveTjeneste oppgaveTjeneste;

    public TilBeslutterOppgaveTransisjon() {
    }

    @Inject
    public TilBeslutterOppgaveTransisjon(KøStatistikkTjeneste køStatistikk,
                                         OppgaveTjeneste oppgaveTjeneste) {
        this.køStatistikk = køStatistikk;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPRETT_BESLUTTEROPPGAVE;
    }

    @Override
    public void håndter(FptilbakeData data) {
        LOG.info("TBK Oppretter beslutteroppgave.");
        var behandlingId = data.hendelse().getBehandlingId();
        var oppgave = oppgaveTjeneste.hentAktivOppgave(behandlingId).orElseThrow();
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveMedEventLogg(oppgave, OppgaveEventType.LUKKET, ReservasjonKonstanter.OPPGAVE_AVSLUTTET);
        var beslutterOppgave = beslutterOppgave(data);
        køStatistikk.lagre(beslutterOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.OPPRETTET)
                .andreKriterierType(TIL_BESLUTTER)
                .behandlendeEnhet(beslutterOppgave.getBehandlendeEnhet())
                .behandlingId(beslutterOppgave.getBehandlingId())
                .build();
        oppgaveTjeneste.lagre(oel);
    }

    private TilbakekrevingOppgave beslutterOppgave(FptilbakeData data) {
        var beslutterOppgave = oppgaveFra(data.hendelse());
        beslutterOppgave.setOppgaveEgenskaper(data.egenskapFinner());
        oppgaveTjeneste.lagre(beslutterOppgave);
        return beslutterOppgave;
    }


}
