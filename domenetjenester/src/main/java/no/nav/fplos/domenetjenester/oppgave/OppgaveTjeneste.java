package no.nav.fplos.domenetjenester.oppgave;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OppgaveTjeneste {

    boolean erAlleOppgaverFortsattTilgjengelig(List<Long> oppgaveIder);

    List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe);

    Oppgave hentOppgave(Long oppgaveId);

    Optional<Oppgave> hentNyesteOppgaveTilknyttet(BehandlingId behandlingId);

}
