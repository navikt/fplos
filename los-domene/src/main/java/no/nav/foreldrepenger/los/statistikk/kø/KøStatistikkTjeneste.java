package no.nav.foreldrepenger.los.statistikk.kø;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringKnytning;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.NyeOgFerdigstilteOppgaver;

@ApplicationScoped
public class KøStatistikkTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(KøStatistikkTjeneste.class);
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkRepository statistikkRepository;

    @Inject
    public KøStatistikkTjeneste(OppgaveKøTjeneste oppgaveKøTjeneste, OppgaveTjeneste oppgaveTjeneste,
                                KøStatistikkRepository statistikkRepository) {
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.statistikkRepository = statistikkRepository;
    }

    public KøStatistikkTjeneste() {
    }

    public List<NyeOgFerdigstilteOppgaver> hentStatistikk(Long køId) {
        return statistikkRepository.hentStatistikk(køId);
    }

    public List<OppgaveFiltreringKnytning> hentOppgaveFiltreringKnytningerForOppgave(Oppgave oppgave) {
        return oppgaveKøTjeneste.finnOppgaveFiltreringKnytninger(oppgave);
    }

    public void lagre(BehandlingId behandlingId, KøOppgaveHendelse køOppgaveHendelse) {
        var nyesteOppgaveTilknyttetBehandling = oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingId);
        LOG.info("Nyeste oppgave tilknyttet behandling er {}", nyesteOppgaveTilknyttetBehandling);
        nyesteOppgaveTilknyttetBehandling
                .filter(Oppgave::getAktiv)
                .ifPresentOrElse(oppgave -> lagre(oppgave, køOppgaveHendelse), () -> LOG.info("Kan ikke lagre statistikk, fant ikke oppgave"));
        // TODO: kalles også når første relevante hendelse er venteaksjonspunkt.
        //  Når det er skilt i hendelsehåndterer kan man kaste exception ved inaktiv oppgave.
    }

    public void lagre(Oppgave oppgave, KøOppgaveHendelse køOppgaveHendelse) {
        if (!oppgave.getAktiv()) {
            throw new IllegalStateException("Oppgave er ikke aktiv, kan ikke fortsette lagring av statistikk.");
        }
        lagreHendelse(oppgave, køOppgaveHendelse);
    }

    public void lagre(OppgaveknytningerFørEtterOppdatering oppgaveknytningerFørEtterOppdatering) {
        oppgaveknytningerFørEtterOppdatering.getInnPåKø().forEach(k -> statistikkRepository.lagre(k.oppgaveId(), k.oppgaveFiltreringId(), k.behandlingType(), KøOppgaveHendelse.INN_FRA_ANNEN_KØ));
        oppgaveknytningerFørEtterOppdatering.getUtAvKø().forEach(k -> statistikkRepository.lagre(k.oppgaveId(), k.oppgaveFiltreringId(), k.behandlingType(), KøOppgaveHendelse.UT_TIL_ANNEN_KØ));
    }

    private void lagreHendelse(Oppgave oppgave, KøOppgaveHendelse køOppgaveHendelse) {
        LOG.info("Lagrer køoppgavehendelse. Oppgave {}, Hendelse {}", oppgave, køOppgaveHendelse);
        var oppgaveFiltreringKnytninger = oppgaveKøTjeneste.finnOppgaveFiltreringKnytninger(oppgave);
        LOG.info("Oppgavefilterknytninger for oppgave {} er {}", oppgave, oppgaveFiltreringKnytninger);
        oppgaveFiltreringKnytninger
                .forEach(ok -> statistikkRepository.lagre(ok.oppgaveId(), ok.oppgaveFiltreringId(), ok.behandlingType(), køOppgaveHendelse));
    }
}
