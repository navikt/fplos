package no.nav.fplos.admin;

import java.util.List;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

public interface AdminTjeneste {

    Oppgave synkroniserOppgave(Long behandlingId);

    Oppgave hentOppgave(Long behandlingId);

    List<OppgaveEventLogg> hentEventer(Long verdi);

    void oppdaterOppgave(Long behandlingId);

    int oppdaterAktiveOppgaver();

    int prosesserAlleMeldingerFraFeillogg();

    List<Oppgave> hentAlleOppgaverForBehandling(Long behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
