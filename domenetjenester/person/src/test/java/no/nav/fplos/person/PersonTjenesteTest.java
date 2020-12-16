package no.nav.fplos.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.vedtak.felles.integrasjon.pdl.PdlKlient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PersonTjenesteTest {
    private static final Person PERSON = FiktivTestPerson.nyPerson();
    private static final List<Person> KJENTE_PERSONER = List.of(PERSON);

    private static final Person UKJENT_PERSON = FiktivTestPerson.nyPerson();

    private static final PersonTjeneste personTjeneste = new PersonTjenesteImpl(mock(PdlKlient.class));

    @Test
    public void skal_ikke_hente_person_for_ukjent_aktør() {
        assertThat(personTjeneste.hentPerson(UKJENT_PERSON.getAktørId())).isEmpty();
    }


    private static class TpsAdapterMock implements TpsAdapter {

        @Override
        public Optional<Person> hentPerson(AktørId aktørId) {
            return KJENTE_PERSONER.stream()
                    .filter(p -> p.getAktørId().equals(aktørId))
                    .findFirst();
        }
    }

    private static class PersonTjenesteDummy implements PersonTjeneste {

        @Override
        public Optional<Person> hentPerson(AktørId aktørId) {
            return Optional.empty();
        }
    }
}
