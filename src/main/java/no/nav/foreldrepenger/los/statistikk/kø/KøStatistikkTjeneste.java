package no.nav.foreldrepenger.los.statistikk.kø;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.oppgave.BehandlingKøRepository;
import no.nav.foreldrepenger.los.oppgave.Filtreringstype;
import no.nav.foreldrepenger.los.oppgave.OppgaveKøRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.Oppgavespørring;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.vedtak.exception.FunksjonellException;

@ApplicationScoped
public class KøStatistikkTjeneste {


    private static final Logger LOG = LoggerFactory.getLogger(KøStatistikkTjeneste.class);

    private OppgaveRepository oppgaveRepository;
    private OppgaveKøRepository oppgaveKøRepository;
    private BehandlingKøRepository behandlingKøRepository;

    KøStatistikkTjeneste() {
        // for CDI
    }

    @Inject
    public KøStatistikkTjeneste(OppgaveRepository oppgaveRepository, OppgaveKøRepository oppgaveKøRepository,
                                BehandlingKøRepository behandlingKøRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveKøRepository = oppgaveKøRepository;
        this.behandlingKøRepository = behandlingKøRepository;
    }

    public List<OppgaveFiltrering> hentAlleOppgavefiltreringer() {
        return oppgaveRepository.hentAlleOppgaveFiltreReadOnly();
    }

    public Integer hentAntallOppgaver(Long behandlingsKø) {
        var oppgavespørring = hentOppgavespørringForKø(behandlingsKø, Filtreringstype.ALLE);
        return oppgaveKøRepository.hentAntallOppgaver(oppgavespørring);
    }

    public Integer hentAntallTilgjengeligeOppgaverFor(Long behandlingsKø) {
        var oppgavespørring = hentOppgavespørringForKø(behandlingsKø, Filtreringstype.LEDIGE);
        return oppgaveKøRepository.hentAntallOppgaver(oppgavespørring);
    }

    public Integer hentAntallVentendeBehandlingerFor(Long behandlingsKø) {
        // TODO: fjerne try-catch når stabil
        try {
            var oppgavespørring = hentOppgavespørringForKø(behandlingsKø, Filtreringstype.ALLE);
            return behandlingKøRepository.hentAntallBehandlingerPåVent(oppgavespørring);
        } catch (Exception e) {
            LOG.warn("Feil ved henting av ventende behandlinger for kø {}", behandlingsKø, e);
            return 0;
        }
    }

    private Oppgavespørring hentOppgavespørringForKø(Long behandlingsKø, Filtreringstype filtreringstype) {
        return oppgaveRepository.hentOppgaveFilterSett(behandlingsKø)
            .map(oppgaveFiltrering -> new Oppgavespørring(oppgaveFiltrering, filtreringstype))
            .orElseThrow(() -> new FunksjonellException("FP-164687", "Fant ikke oppgavekø med id " + behandlingsKø));
    }
}
