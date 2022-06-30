package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavehendelseHåndterer.FptilbakeData;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.LUKK_OPPGAVE;

@ApplicationScoped
public class LukkOppgaveTransisjon implements FptilbakeOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(LukkOppgaveTransisjon.class);

    private KøStatistikkTjeneste køStatistikk;
    private OppgaveTjeneste oppgaveTjeneste;


    public LukkOppgaveTransisjon() {
    }

    @Inject
    public LukkOppgaveTransisjon(KøStatistikkTjeneste køStatistikk, OppgaveTjeneste oppgaveTjeneste) {
        this.køStatistikk = køStatistikk;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return LUKK_OPPGAVE;
    }

    @Override
    public void håndter(FptilbakeData data) {
        var behandlingId = data.hendelse().getBehandlingId();
        LOG.info("TBK Lukker oppgave med behandlingId {}.", behandlingId.toString());
        avsluttOppgaveForBehandling(behandlingId);
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.LUKKET)
                .behandlendeEnhet(data.hendelse().getBehandlendeEnhet())
                .behandlingId(behandlingId)
                .build();
        oppgaveTjeneste.lagre(oel);
    }


    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
    }

}
