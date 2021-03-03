package no.nav.foreldrepenger.los.klient.person;


import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;

import java.util.Optional;

public interface PersonTjeneste {
    Optional<Person> hentPerson(AktørId aktørId);
}
