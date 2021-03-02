package no.nav.fplos.domenetjenester.statistikk_ny;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.KøOppgaveHendelse;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.KøStatistikk;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.NyOpppgaveStatistikkRepository;

import no.nav.fplos.domenetjenester.kø.OppgaveKøTjeneste;
import no.nav.fplos.domenetjenester.oppgave.OppgaveTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class OppgaveStatistikk {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveStatistikk.class);
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private NyOpppgaveStatistikkRepository statistikkRepository;

    @Inject
    public OppgaveStatistikk(OppgaveKøTjeneste oppgaveKøTjeneste, OppgaveTjeneste oppgaveTjeneste,
                             NyOpppgaveStatistikkRepository statistikkRepository) {
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.statistikkRepository = statistikkRepository;
    }

    public OppgaveStatistikk() {
    }

    public void lagre(BehandlingId behandlingId, KøOppgaveHendelse køOppgaveHendelse) {
        oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingId)
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

    public List<KøStatistikk> hentStatistikk(Long køId) {
        var liste = statistikkRepository.hentStatistikk(køId);
        LOG.info("Hentet liste: {}", liste);
        return liste;
    }

    private void lagreHendelse(Oppgave oppgave, KøOppgaveHendelse køOppgaveHendelse) {
        oppgaveKøTjeneste.finnOppgaveFiltreringKnytninger(oppgave)
                .forEach(ok -> statistikkRepository.lagre(ok.getOppgaveId(), ok.getOppgaveFiltreringId(), ok.getBehandlingType(), køOppgaveHendelse));
    }
}
