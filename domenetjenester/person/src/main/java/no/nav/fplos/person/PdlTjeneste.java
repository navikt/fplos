package no.nav.fplos.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;

import java.util.Optional;

public interface PdlTjeneste {
    Optional<Person> hentPerson(AktørId aktørId);
}
