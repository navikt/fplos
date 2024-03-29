package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class LukkOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(LukkOppgaveOppgavetransisjonHåndterer.class);
    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkTjeneste køStatistikk;

    @Inject
    public LukkOppgaveOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste, KøStatistikkTjeneste køStatistikk) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.køStatistikk = køStatistikk;
    }

    public LukkOppgaveOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk) {
        LOG.info("Håndterer hendelse for å lukke oppgave, behandling {}, system {}", behandlingId, SYSTEM);
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
        var oel = OppgaveEventLogg.builder()
            .behandlendeEnhet(behandling.behandlendeEnhetId())
            .type(OppgaveEventType.LUKKET)
            .behandlingId(behandlingId)
            .build();
        oppgaveTjeneste.lagre(oel);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.LUKK_OPPGAVE;
    }
}
