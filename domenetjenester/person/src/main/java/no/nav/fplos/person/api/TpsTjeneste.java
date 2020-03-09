package no.nav.fplos.person.api;

import java.util.Optional;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;


public interface TpsTjeneste {

    Optional<TpsPersonDto> hentBrukerForAktør(AktørId aktørId);

    Optional<TpsPersonDto> hentBrukerForFnr(PersonIdent fnr);
}
