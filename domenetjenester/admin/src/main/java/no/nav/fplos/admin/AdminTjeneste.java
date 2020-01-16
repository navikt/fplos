package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;

import java.util.List;
import java.util.UUID;

public interface AdminTjeneste {

    Oppgave synkroniserOppgave(UUID uuid);

    Oppgave hentOppgave(UUID uuid);

    TilbakekrevingOppgave hentTilbakekrevingOppgave(UUID uuid);

    List<OppgaveEventLogg> hentEventer(Long verdi);
    List<OppgaveEventLogg> hentEventer(UUID uuid);

    void oppdaterOppgave(UUID uuid);

    int oppdaterAktiveOppgaver();

    int prosesserAlleMeldingerFraFeillogg();

    List<Oppgave> hentAlleOppgaverForBehandling(UUID uuid);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
