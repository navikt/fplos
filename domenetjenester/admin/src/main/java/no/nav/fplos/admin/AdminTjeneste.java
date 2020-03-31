package no.nav.fplos.admin;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

public interface AdminTjeneste {

    Oppgave synkroniserOppgave(BehandlingId behandlingId);

    Oppgave hentOppgave(BehandlingId behandlingId);

    List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId);

    void oppdaterOppgave(BehandlingId behandlingId);

    int prosesserAlleMeldingerFraFeillogg();

    Optional<EventmottakFeillogg> ferdigmarkerOgHentOppgaveEvent(Long verdi);

    List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
