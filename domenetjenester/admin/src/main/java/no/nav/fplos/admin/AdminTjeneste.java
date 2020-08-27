package no.nav.fplos.admin;

import java.util.List;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;

public interface AdminTjeneste {

    void opprettAvdeling(Avdeling enhet);

    Oppgave synkroniserOppgave(BehandlingId behandlingId);

    Oppgave hentOppgave(BehandlingId behandlingId);

    List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId);

    List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
