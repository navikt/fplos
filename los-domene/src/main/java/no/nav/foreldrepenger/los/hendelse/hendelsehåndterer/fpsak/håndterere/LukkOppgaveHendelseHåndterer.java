package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

@SuppressWarnings("ClassCanBeRecord")
public class LukkOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(LukkOppgaveHendelseHåndterer.class);
    private final OppgaveTjeneste oppgaveTjeneste;
    private final KøStatistikkTjeneste køStatistikk;
    private final BehandlingFpsak behandlingFpsak;

    public LukkOppgaveHendelseHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                        KøStatistikkTjeneste køStatistikk,
                                        BehandlingFpsak behandlingFpsak) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.køStatistikk = køStatistikk;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    public void håndter() {
        var behandlingId = behandlingFpsak.getBehandlingId();
        LOG.info("Håndterer hendelse for å lukke oppgave, behandling {}, system {}", behandlingId,  SYSTEM);
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveForBehandling(behandlingId);
        var oel = OppgaveEventLogg.builder()
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.LUKKET)
                .behandlingId(behandlingId)
                .build();
        oppgaveTjeneste.lagre(oel);
    }
}
