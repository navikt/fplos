package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakStatus;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;

import java.util.List;
import java.util.UUID;

public interface AdminRepository {
    void deaktiverSisteOppgave(Long behandlingId);

    Oppgave hentSisteOppgave(Long behandlingId);

    TilbakekrevingOppgave hentSisteTilbakekrevingOppgave(UUID behandlingId);

    List<OppgaveEventLogg> hentEventer(Long behandlingId);

    List<Oppgave> hentAlleAktiveOppgaver();

    List<EventmottakFeillogg> hentAlleMeldingerFraFeillogg();

    void oppdaterStatus(Long feilloggId, EventmottakStatus status);

    List<Oppgave> hentAlleOppgaverForBehandling(Long behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
