package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

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
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class OpprettPapirsøknadOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(OpprettPapirsøknadOppgaveOppgavetransisjonHåndterer.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;

    @Inject
    public OpprettPapirsøknadOppgaveOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                               OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
    }

    public OpprettPapirsøknadOppgaveOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk) {
        håndterEksisterendeOppgave(behandlingId);
        var oppgave = OppgaveUtil.oppgave(behandlingId, behandling);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, new FpsakOppgaveEgenskapFinner(behandling));
        oppgaveTjeneste.lagre(oppgave);
        opprettOppgaveEventLogg(oppgave);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPRETT_PAPIRSØKNADOPPGAVE;
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
        LOG.info("Oppretter {}-oppgave med id {} og av type {}", SYSTEM, oppgave.getId(), AndreKriterierType.PAPIRSØKNAD.getKode());
    }

}
