package no.nav.fplos.domenetjenester.admin;

import java.util.List;

import no.nav.foreldrepenger.domene.typer.Saksnummer;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;

public interface AdminTjeneste {

    List<Oppgave> hentOppgaver(Saksnummer saksnummer);

    List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId);

    List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);
}
