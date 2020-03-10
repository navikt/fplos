package no.nav.fplos.person.api;

import java.util.Optional;

import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;


public interface TpsTjeneste {

    TpsPersonDto hentBrukerForAktør(long aktørId) throws HentPersonSikkerhetsbegrensning;

    Optional<TpsPersonDto> hentBrukerForFnr(PersonIdent fnr);
}
