package no.nav.foreldrepenger.los.oppgave;


import no.nav.foreldrepenger.los.domene.typer.BehandlingId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OppgaveTjeneste {

    boolean erAlleOppgaverFortsattTilgjengelig(List<Long> oppgaveIder);

    List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe);

    Oppgave hentOppgave(Long oppgaveId);

    Optional<Oppgave> hentNyesteOppgaveTilknyttet(BehandlingId behandlingId);

}
