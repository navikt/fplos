package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

import java.util.List;
import java.util.UUID;

public interface AdminRepository {

    void deaktiverSisteOppgave(UUID uuid);

    Oppgave hentSisteOppgave(UUID uuid);

    List<OppgaveEventLogg> hentEventer(UUID uuid);

    List<Oppgave> hentAlleAktiveOppgaver();

    List<EventmottakFeillogg> hentAlleMeldingerFraFeillogg();

    void markerFerdig(Long feilloggId);

    List<Oppgave> hentAlleOppgaverForBehandling(UUID uuid);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
