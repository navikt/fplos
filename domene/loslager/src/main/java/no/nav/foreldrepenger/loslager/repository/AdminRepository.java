package no.nav.foreldrepenger.loslager.repository;

import java.util.List;

import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

public interface AdminRepository {
    void deaktiverSisteOppgave(Long behandlingId);

    Oppgave hentSisteOppgave(Long behandlingId);

    List<OppgaveEventLogg> hentEventer(Long behandlingId);

    List<Oppgave> hentAlleAktiveOppgaver();

    List<EventmottakFeillogg> hentAlleMeldingerFraFeillogg();

    void markerFerdig(Long feilloggId);

    List<Oppgave> hentAlleOppgaverForBehandling(Long behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
