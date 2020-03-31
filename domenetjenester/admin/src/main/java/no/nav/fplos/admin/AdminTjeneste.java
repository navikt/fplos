package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

import java.util.List;

public interface AdminTjeneste {

    Oppgave synkroniserOppgave(BehandlingId behandlingId);

    Oppgave hentOppgave(BehandlingId behandlingId);

    List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId);

    void oppdaterOppgave(BehandlingId behandlingId);

    int prosesserAlleMeldingerFraFeillogg();

    void ferdigmarkerOgHentOppgaveEvent(Long verdi);

    List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
