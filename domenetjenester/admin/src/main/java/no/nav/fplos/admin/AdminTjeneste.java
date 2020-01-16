package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;

import java.util.List;
import java.util.UUID;

public interface AdminTjeneste {

    Oppgave synkroniserOppgave(Long behandlingId);

    Oppgave hentOppgave(Long behandlingId);

    TilbakekrevingOppgave hentTilbakekrevingOppgave(UUID uuid);

    List<OppgaveEventLogg> hentEventer(Long verdi);

    void oppdaterOppgave(Long behandlingId);

    int oppdaterAktiveOppgaver();

    int prosesserAlleMeldingerFraFeillogg();

    List<Oppgave> hentAlleOppgaverForBehandling(Long behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
