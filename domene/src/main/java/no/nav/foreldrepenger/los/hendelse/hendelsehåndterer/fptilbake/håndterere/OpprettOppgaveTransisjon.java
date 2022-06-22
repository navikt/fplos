package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.OppgaveUtil.oppgaveFra;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavehendelseHåndterer.FptilbakeData;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

// håndterer hendelser med aktive aksjonspunkt og det ikke finnes åpen oppgave
@ApplicationScoped
public class OpprettOppgaveTransisjon implements FptilbakeOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OpprettOppgaveTransisjon.class);
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private KøStatistikkTjeneste køStatistikkTjeneste;

    private OppgaveTjeneste oppgaveTjeneste;


    public OpprettOppgaveTransisjon() {
    }

    public OpprettOppgaveTransisjon(OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                    KøStatistikkTjeneste køStatistikkTjeneste,
                                    OppgaveTjeneste oppgaveTjeneste) {
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.køStatistikkTjeneste = køStatistikkTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @Override
    public void håndter(FptilbakeData data) {
        sjekkAktivOppgave(data);
        opprettOppgave(data);
    }

    private void opprettOppgave(FptilbakeData data) {
        var aksjonspunkter = data.hendelse().getAksjonspunkter();
        var ansvarligSaksbehandler = data.hendelse().getAnsvarligSaksbehandler();
        var egenskapFinner = new FptilbakeOppgaveEgenskapFinner(aksjonspunkter, ansvarligSaksbehandler);
        boolean erBeslutteroppgave = egenskapFinner.getAndreKriterier().contains(AndreKriterierType.TIL_BESLUTTER);
        TilbakekrevingOppgave oppgave = oppgaveTjeneste.lagre(oppgaveFra(data.hendelse()));
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
        køStatistikkTjeneste.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
        loggOpprettelse(oppgave, erBeslutteroppgave);
        LOG.info("TBK Oppretter oppgave {} for behandlingId {}.", oppgave.getId(), oppgave.getBehandlingId());
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPRETT_OPPGAVE;
    }

    private void loggOpprettelse(Oppgave oppgave, boolean erBeslutteroppgave) {
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.OPPRETTET)
                .behandlendeEnhet(oppgave.getBehandlendeEnhet())
                .behandlingId(oppgave.getBehandlingId())
                .andreKriterierType(erBeslutteroppgave ? AndreKriterierType.TIL_BESLUTTER : null)
                .build();
        oppgaveTjeneste.lagre(oel);
    }

    private void sjekkAktivOppgave(FptilbakeData data) {
        var aktivOppgave = oppgaveTjeneste.hentAktivOppgave(data.hendelse().getBehandlingId());
        if (aktivOppgave.isPresent()) {
            throw new IllegalStateException("TBK: fant eksisterende oppgave tilknyttet behandlingId " + data.hendelse().getBehandlingId());
        }
    }


}
