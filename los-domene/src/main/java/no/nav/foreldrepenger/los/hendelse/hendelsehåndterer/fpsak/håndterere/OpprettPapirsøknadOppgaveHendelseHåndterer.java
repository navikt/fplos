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
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;

public class OpprettPapirsøknadOppgaveHendelseHåndterer extends OpprettOppgaveHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(OpprettPapirsøknadOppgaveHendelseHåndterer.class);

    private final OppgaveRepository oppgaveRepository;
    private final BehandlingFpsak behandlingFpsak;

    public OpprettPapirsøknadOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                                      OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                      OppgaveStatistikk oppgaveStatistikk,
                                                      BehandlingFpsak behandlingFpsak) {
        super(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
        this.oppgaveRepository = oppgaveRepository;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    void håndterEksisterendeOppgave() {
        oppgaveRepository.hentOppgaver(behandlingFpsak.getBehandlingId())
                .stream()
                .filter(Oppgave::getAktiv)
                .findFirst()
                .ifPresent(o -> {
                    throw new IllegalStateException(String.format("Finnes aktiv oppgave (oppgaveId %s) fra før, "
                                    + "gir opp håndtering av hendelse. Sannsynlig feil i logikk i utledning av FpsakHendelseHåndterer",
                            o.getId()));
                });
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
                AndreKriterierType.PAPIRSØKNAD.getNavn());
    }

}
