package no.nav.foreldrepenger.los.admin;

import java.util.List;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

public interface AdminRepository {

    List<Oppgave> hentOppgaver(Saksnummer saksnummer);

    List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId);

    List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId);

    Oppgave deaktiverOppgave(Long oppgaveId);

    Oppgave aktiverOppgave(Long oppgaveId);

}
