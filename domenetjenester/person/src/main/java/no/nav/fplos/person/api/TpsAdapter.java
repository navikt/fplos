package no.nav.fplos.person.api;

import no.nav.foreldrepenger.loslager.aktør.GeografiskTilknytning;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;

import java.util.Optional;

public interface TpsAdapter {

    Optional<AktørId> hentAktørIdForPersonIdent(PersonIdent fnr);

    Optional<PersonIdent> hentIdentForAktørId(AktørId aktørId);

    TpsPersonDto hentKjerneinformasjon(PersonIdent fnr, AktørId aktørId);

    GeografiskTilknytning hentGeografiskTilknytning(String fnr);

}
