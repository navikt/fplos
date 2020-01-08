package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

import java.util.List;
import java.util.UUID;

public interface AdminTjeneste {

    Oppgave synkroniserOppgave(Long behandlingId);

    @Deprecated
    Oppgave hentOppgave(Long behandlingId);

    Oppgave hentOppgave(UUID uuid);

    @Deprecated
    List<OppgaveEventLogg> hentEventer(Long behandlingId);

    List<OppgaveEventLogg> hentEventer(UUID uuid);

    @Deprecated
    void oppdaterOppgave(Long behandlingId);

    void oppdaterOppgave(UUID uuid);

    int oppdaterAktiveOppgaver();

    int prosesserAlleMeldingerFraFeillogg();

    int oppdaterAktiveOppgaverMedInformasjonOmRefusjonskrav();

    int oppdaterAktiveOppgaverMedInformasjonHvisUtlandssak();

    int oppdaterAktiveOppgaverMedInformasjonHvisGradering();

    @Deprecated
    List<Oppgave> hentAlleOppgaverForBehandling(Long behandlingId);

    List<Oppgave> hentAlleOppgaverForBehandling(UUID uuid);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
