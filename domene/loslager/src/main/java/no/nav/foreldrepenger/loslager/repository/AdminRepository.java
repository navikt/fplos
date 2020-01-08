package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

import java.util.List;
import java.util.UUID;

public interface AdminRepository {
    @Deprecated
    void deaktiverSisteOppgave(Long behandlingId);

    void deaktiverSisteOppgave(UUID uuid);

    @Deprecated
    Oppgave hentSisteOppgave(Long behandlingId);

    Oppgave hentSisteOppgave(UUID uuid);

    @Deprecated
    List<OppgaveEventLogg> hentEventer(Long behandlingId);

    List<OppgaveEventLogg> hentEventer(UUID uuid);

    List<Oppgave> hentAlleAktiveOppgaver();

    List<EventmottakFeillogg> hentAlleMeldingerFraFeillogg();

    void markerFerdig(Long feilloggId);

    @Deprecated
    List<Oppgave> hentAlleOppgaverForBehandling(Long behandlingId);

    List<Oppgave> hentAlleOppgaverForBehandling(UUID uuid);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
