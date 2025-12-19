package no.nav.foreldrepenger.los.statistikk.kø;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.oppgave.Filtreringstype;
import no.nav.foreldrepenger.los.oppgave.OppgaveKøRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.Oppgavespørring;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.vedtak.exception.FunksjonellException;

@ApplicationScoped
public class StatistikkTjeneste {

    private OppgaveRepository oppgaveRepository;
    private OppgaveKøRepository oppgaveKøRepository;

    StatistikkTjeneste() {
        // for CDI
    }

    @Inject
    public StatistikkTjeneste(OppgaveRepository oppgaveRepository, OppgaveKøRepository oppgaveKøRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveKøRepository = oppgaveKøRepository;
    }

    public List<OppgaveFiltrering> hentAlleKøer() {
        return oppgaveRepository.hentAlleOppgaveFiltre();
    }

    public Integer hentAntallOppgaver(Long behandlingsKø) {
        var oppgavespørring = hentOppgavespørringForKø(behandlingsKø, Filtreringstype.AKTIVE);
        return oppgaveKøRepository.hentAntallOppgaver(oppgavespørring);
    }

    public Integer hentAntallTilgjengeligeOppgaverFor(Long behandlingsKø) {
        var oppgavespørring = hentOppgavespørringForKø(behandlingsKø, Filtreringstype.AKTIVE_OG_LEDIGE);
        return oppgaveKøRepository.hentAntallOppgaver(oppgavespørring);
    }

    private Oppgavespørring hentOppgavespørringForKø(Long behandlingsKø, Filtreringstype filtreringstype) {
        return oppgaveRepository.hentOppgaveFilterSett(behandlingsKø)
            .map(oppgaveFiltrering -> new Oppgavespørring(oppgaveFiltrering, filtreringstype))
            .orElseThrow(() -> new FunksjonellException("FP-164687", "Fant ikke oppgavekø med id " + behandlingsKø));
    }
}
