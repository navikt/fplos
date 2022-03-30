package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

public class GenerellOpprettOppgaveHendelseHåndterer extends OpprettOppgaveHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(GenerellOpprettOppgaveHendelseHåndterer.class);

    private final OppgaveTjeneste oppgaveTjeneste;
    private final BehandlingFpsak behandlingFpsak;

    public GenerellOpprettOppgaveHendelseHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                   OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                   KøStatistikkTjeneste køStatistikk,
                                                   BehandlingFpsak behandlingFpsak) {
        super(oppgaveTjeneste, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak);
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    void håndterEksisterendeOppgave() {
        oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingFpsak.getBehandlingId())
                .filter(Oppgave::getAktiv)
                .ifPresent(o -> {
                    throw new IllegalStateException(
                            String.format("Finnes aktiv oppgave (oppgaveId %s) fra før, gir opp håndtering av hendelse",
                                    o.getId()));
                });
    }

    @Override
    void opprettOppgaveEventLogg(Oppgave oppgave) {
        var oel = OppgaveEventLogg.opprettetOppgaveEvent(oppgave);
        oppgaveTjeneste.lagre(oel);
        LOG.info("Oppretter {}-oppgave med id {}", FpsakHendelseHåndterer.SYSTEM, oppgave.getId());
    }
}
