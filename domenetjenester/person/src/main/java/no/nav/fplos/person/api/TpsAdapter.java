package no.nav.fplos.person.api;

import java.util.Optional;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;

public interface TpsAdapter {

    Optional<AktørId> hentAktørIdForPersonIdent(PersonIdent fnr);

    Optional<PersonIdent> hentIdentForAktørId(AktørId aktørId);

    TpsPersonDto hentKjerneinformasjon(PersonIdent fnr, AktørId aktørId);

}
