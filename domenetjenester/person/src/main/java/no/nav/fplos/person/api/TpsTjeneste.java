package no.nav.fplos.person.api;

import no.nav.foreldrepenger.loslager.aktør.GeografiskTilknytning;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.vedtak.exception.TekniskException;

import java.util.Optional;


public interface TpsTjeneste {

    Optional<TpsPersonDto> hentBrukerForAktør(AktørId aktørId);

    /**
     * Hent PersonIdent (FNR) for gitt aktørId.
     * 
     * @throws TekniskException hvis ikke finner.
     */
    PersonIdent hentFnrForAktør(AktørId aktørId);

    Optional<TpsPersonDto> hentBrukerForFnr(PersonIdent fnr);

    Optional<AktørId> hentAktørForFnr(PersonIdent fnr);

    GeografiskTilknytning hentGeografiskTilknytning(String fnr);
}
