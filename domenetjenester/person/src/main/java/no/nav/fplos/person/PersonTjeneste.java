package no.nav.fplos.person;

import java.util.Optional;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;


public interface PersonTjeneste {

    Optional<Person> hentPerson(AktørId aktørId);

    Optional<Person> hentPerson(Fødselsnummer fnr);
}
