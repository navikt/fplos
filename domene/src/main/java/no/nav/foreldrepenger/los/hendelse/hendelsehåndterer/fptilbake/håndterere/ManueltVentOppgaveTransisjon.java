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

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavetransisjonHåndterer.Oppgavetransisjon.LUKK_OPPGAVE;

@ApplicationScoped
public class ManueltVentOppgaveTransisjon implements FptilbakeOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(ManueltVentOppgaveTransisjon.class);

    private KøStatistikkTjeneste køStatistikk;
    private OppgaveTjeneste oppgaveTjeneste;


    public ManueltVentOppgaveTransisjon() {
    }

    public ManueltVentOppgaveTransisjon(KøStatistikkTjeneste køStatistikk, OppgaveTjeneste oppgaveTjeneste) {
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
        LOG.info("TBK Lukker oppgave tilknyttet behandlingId {}, satt manuelt på vent.", behandlingId.toString());
        avsluttOppgaveForBehandling(behandlingId);
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.MANU_VENT)
                .behandlendeEnhet(data.hendelse().getBehandlendeEnhet())
                .behandlingId(behandlingId)
                .build();
        oppgaveTjeneste.lagre(oel);
    }


    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.OPPGAVE_SATT_PÅ_VENT);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
    }

}
