package no.nav.fplos.domenetjenester.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;

import java.util.Optional;

public interface PersonTjeneste {
    Optional<Person> hentPerson(AktørId aktørId);
}
