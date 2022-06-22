package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.OppgaveUtil.oppgaveFra;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgaveEgenskapFinner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavehendelseHåndterer.FptilbakeData;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

@ApplicationScoped
public class ReturFraBeslutterOppgaveTransisjon implements FptilbakeOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(ReturFraBeslutterOppgaveTransisjon.class);
    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkTjeneste køStatistikk;
    private ReservasjonTjeneste reservasjonTjeneste;

    public ReturFraBeslutterOppgaveTransisjon() {
    }

    public ReturFraBeslutterOppgaveTransisjon(OppgaveTjeneste oppgaveTjeneste,
                                              KøStatistikkTjeneste køStatistikk,
                                              ReservasjonTjeneste reservasjonTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.køStatistikk = køStatistikk;
        this.reservasjonTjeneste = reservasjonTjeneste;
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.RETUR_FRA_BESLUTTER_OPPGAVE;
    }

    @Override
    public void håndter(FptilbakeData data) {
        håndterEksisterendeOppgave(data);
        var saksbehandlerOppgave = opprettOppgave(data);
        var oel = OppgaveEventLogg.builder()
                .behandlingId(data.hendelse().getBehandlingId())
                .behandlendeEnhet(data.hendelse().getBehandlendeEnhet())
                .type(OppgaveEventType.OPPRETTET)
                .build();
        oppgaveTjeneste.lagre(oel);
        køStatistikk.lagre(saksbehandlerOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }


    private Oppgave opprettOppgave(FptilbakeData data) {
        var oppgave = oppgaveFra(data.hendelse());
        oppgave.setOppgaveEgenskaper(data.egenskapFinner());
        oppgaveTjeneste.lagre(oppgave);
        reservasjonTjeneste.opprettReservasjon(oppgave, data.hendelse().getAnsvarligSaksbehandler(), ReservasjonKonstanter.RETUR_FRA_BESLUTTER);
        LOG.info("Retur fra beslutter, oppretter oppgave og flytter reservasjon til ansvarlig saksbehandler");
        return oppgave;
    }

    private void håndterEksisterendeOppgave(FptilbakeData data) {
        var behandlingId = data.hendelse().getBehandlingId();
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
        var oel = OppgaveEventLogg.builder()
                .behandlingId(behandlingId)
                .behandlendeEnhet(data.hendelse().getBehandlendeEnhet())
                .type(OppgaveEventType.LUKKET)
                .build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Avslutter {} beslutteroppgave", SYSTEM);
    }
}
