package no.nav.foreldrepenger.los.admin;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;

import java.util.List;

public interface AdminRepository {

    void deaktiverSisteOppgave(BehandlingId behandlingId);

    Oppgave hentSisteOppgave(BehandlingId behandlingId);

    List<Oppgave> hentOppgaver(Saksnummer saksnummer);

    List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId);

    List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);

}
