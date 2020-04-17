package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import java.util.List;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;

public interface FagsakApplikasjonTjeneste {

    List<FagsakDto> hentSaker(String søkestreng);

    String hentNavnHvisReservertAvAnnenSaksbehandler(Oppgave oppgave);
}
