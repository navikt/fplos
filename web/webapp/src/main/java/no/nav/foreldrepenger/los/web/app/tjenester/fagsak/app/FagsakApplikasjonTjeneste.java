package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;

import java.util.List;

public interface FagsakApplikasjonTjeneste {

    List<FagsakDto> hentSaker(String søkestreng);

    List<Oppgave> hentOppgaverForSaksnummer(Long fagsakSaksnummer);

    String hentNavnHvisReservertAvAnnenSaksbehandler(Oppgave oppgave);
}
