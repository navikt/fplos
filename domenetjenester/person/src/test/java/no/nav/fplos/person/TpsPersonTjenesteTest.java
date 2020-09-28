package no.nav.fplos.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TpsPersonTjenesteTest {
    private static final Person PERSON = FiktivTestPerson.nyPerson();
    private static final List<Person> KJENTE_PERSONER = List.of(PERSON);

    private static final Person UKJENT_PERSON = FiktivTestPerson.nyPerson();

    private static final PersonTjeneste personTjeneste = new TpsPersonTjeneste(new TpsAdapterMock());


    @Test
    public void skal_ikke_hente_person_for_ukjent_aktør() {
        assertThat(personTjeneste.hentPerson(UKJENT_PERSON.getAktørId())).isEmpty();
    }

    @Test
    public void skal_hente_person_for_kjent_fnr() {
        Optional<Person> funnetPerson = personTjeneste.hentPerson(PERSON.getFødselsnummer());
        assertThat(funnetPerson.isPresent()).isTrue();
        assertThat(funnetPerson.get()).isEqualTo(PERSON);
    }

    @Test
    public void skal_ikke_hente_person_for_ukjent_fnr() {
        Optional<Person> ukjentPerson = personTjeneste.hentPerson(UKJENT_PERSON.getFødselsnummer());
        assertThat(ukjentPerson.isPresent()).isFalse();
    }

    private static class TpsAdapterMock implements TpsAdapter {

        @Override
        public Optional<AktørId> hentAktørForFødselsnummer(Fødselsnummer fnr) {
            return KJENTE_PERSONER.stream()
                    .filter(p -> p.getFødselsnummer().equals(fnr))
                    .map(Person::getAktørId)
                    .findFirst();
        }

        @Override
        public Optional<Person> hentPerson(AktørId aktørId) {
            return KJENTE_PERSONER.stream()
                    .filter(p -> p.getAktørId().equals(aktørId))
                    .findFirst();
        }
    }
}
