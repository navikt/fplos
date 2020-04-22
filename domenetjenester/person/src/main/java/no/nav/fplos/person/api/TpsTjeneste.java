package no.nav.fplos.person.api;

import java.util.Optional;

import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;


public interface TpsTjeneste {

    TpsPersonDto hentBrukerForAktør(long aktørId);

    Optional<TpsPersonDto> hentBrukerForFnr(PersonIdent fnr);
}
