package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

public class OpprettPapirsøknadOppgaveHendelseHåndterer extends OpprettOppgaveHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(OpprettPapirsøknadOppgaveHendelseHåndterer.class);

    private final OppgaveRepository oppgaveRepository;
    private final BehandlingFpsak behandlingFpsak;

    public OpprettPapirsøknadOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                                      OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                      KøStatistikkTjeneste køStatistikk,
                                                      BehandlingFpsak behandlingFpsak) {
        super(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak);
        this.oppgaveRepository = oppgaveRepository;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    void håndterEksisterendeOppgave() {
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingFpsak.getBehandlingId());
    }

    @Override
    void opprettOppgaveEventLogg(Oppgave oppgave) {
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.OPPRETTET)
                .andreKriterierType(AndreKriterierType.PAPIRSØKNAD)
                .behandlendeEnhet(oppgave.getBehandlendeEnhet())
                .behandlingId(oppgave.getBehandlingId())
                .build();
        oppgaveRepository.lagre(oel);
        LOG.info("Oppretter {}-oppgave med id {} og av type {}", SYSTEM, oppgave.getId(),
                AndreKriterierType.PAPIRSØKNAD.getKode());
    }

}
