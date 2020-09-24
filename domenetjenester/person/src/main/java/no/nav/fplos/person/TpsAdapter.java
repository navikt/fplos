package no.nav.fplos.person;

import java.util.Optional;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;

public interface TpsAdapter {

    Optional<AktørId> hentAktørForFødselsnummer(Fødselsnummer fnr);

    Optional<Person> hentPerson(AktørId aktørId);

}
