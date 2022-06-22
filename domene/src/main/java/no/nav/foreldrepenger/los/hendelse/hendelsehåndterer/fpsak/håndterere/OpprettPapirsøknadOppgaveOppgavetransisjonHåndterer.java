package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;

@ApplicationScoped
public class OpprettPapirsøknadOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(OpprettPapirsøknadOppgaveOppgavetransisjonHåndterer.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkTjeneste køStatistikk;

    @Inject
    public OpprettPapirsøknadOppgaveOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                               KøStatistikkTjeneste køStatistikk) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.køStatistikk = køStatistikk;
    }

    public OpprettPapirsøknadOppgaveOppgavetransisjonHåndterer() {
    }

    private void håndterEksisterendeOppgave(BehandlingId behandlingId) {
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
    }

    private void opprettOppgaveEventLogg(Oppgave oppgave) {
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.OPPRETTET)
                .andreKriterierType(AndreKriterierType.PAPIRSØKNAD)
                .behandlendeEnhet(oppgave.getBehandlendeEnhet())
                .behandlingId(oppgave.getBehandlingId())
                .build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Oppretter {}-oppgave med id {} og av type {}", SYSTEM, oppgave.getId(),
                AndreKriterierType.PAPIRSØKNAD.getKode());
    }

    @Override
    public void håndter(BehandlingFpsak behandlingFpsak) {
        håndterEksisterendeOppgave(behandlingFpsak.getBehandlingId());
        var oppgave = opprettOppgave(behandlingFpsak);
        opprettOppgaveEventLogg(oppgave);
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPRETT_PAPIRSØKNADOPPGAVE;
    }

    private Oppgave opprettOppgave(BehandlingFpsak behandlingFpsak) {
        var oppgave = oppgave(behandlingFpsak);
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgave.setOppgaveEgenskaper(egenskapFinner);
        oppgaveTjeneste.lagre(oppgave);
        return oppgave;
    }

}
