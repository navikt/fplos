package no.nav.fplos.kø;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgavekø.Oppgavekø;

import java.util.List;

public interface OppgaveKøTjeneste {

    List<Oppgave> hentOppgaver(Long sakslisteId);

    List<Oppgave> hentOppgaver(Long sakslisteId, int maksAntall);

    List<OppgaveFiltrering> hentAlleOppgaveFiltrering(String brukerIdent);

    List<OppgaveFiltrering> hentOppgaveFiltreringerForPåloggetBruker();

    List<Oppgavekø> finnKøerSomInneholder(Oppgave oppgave);

    Integer hentAntallOppgaver(Long behandlingsKø, boolean forAvdelingsleder);

    Integer hentAntallOppgaverForAvdeling(String avdelingEnhet);

}
