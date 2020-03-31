package no.nav.foreldrepenger.loslager.repository;

import java.util.List;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

public interface AdminRepository {

    void deaktiverSisteOppgave(BehandlingId behandlingId);

    Oppgave hentSisteOppgave(BehandlingId behandlingId);

    List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId);

    EventmottakFeillogg hentEvent(Long id);

    List<EventmottakFeillogg> hentAlleMeldingerFraFeillogg();

    void markerFerdig(Long feilloggId);

    List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
