package no.nav.fplos.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TpsPersonTjenesteTest {
    private static final Person PERSON = FiktivTestPerson.ny();
    private static final List<Person> KJENTE_PERSONER = List.of(PERSON);

    private PersonTjeneste personTjeneste;

    @BeforeEach
    public void oppsett() {
        personTjeneste = new TpsPersonTjeneste(new TpsAdapterMock());
    }

    @Test
    public void skal_ikke_hente_bruker_for_ukjent_aktør() {
        var ukjentAktør = new AktørId(666L);
        assertThat(personTjeneste.hentPerson(ukjentAktør)).isEmpty();
    }

    @Test
    public void skal_hente_bruker_for_kjent_fnr() {
        Optional<Person> funnetPerson = personTjeneste.hentPerson(PERSON.getFødselsnummer());
        assertThat(funnetPerson.isPresent()).isTrue();
        assertThat(funnetPerson.get()).isEqualTo(PERSON);
    }

    @Test
    public void skal_ikke_hente_bruker_for_ukjent_fnr() {
        Optional<Person> funnetBruker = personTjeneste.hentPerson(new Fødselsnummer("66666666666"));
        assertThat(funnetBruker.isPresent()).isFalse();
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
