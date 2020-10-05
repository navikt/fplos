package no.nav.fplos.person;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn;

@ExtendWith(MockitoExtension.class)
public class TpsMapperTest {

    @Test
    public void skal_oversette_til_person() {
        Person testPerson = FiktivTestPerson.nyPerson();
        Bruker tpsBruker = lagTpsBruker(testPerson);
        Person person = TpsMapper.tilPerson(testPerson.getAktørId(), tpsBruker);

        assertThat(person.getNavn()).isEqualTo(testPerson.getNavn());
        assertThat(person.getFødselsnummer()).isEqualTo(testPerson.getFødselsnummer());
    }

    private Bruker lagTpsBruker(Person testPerson) {
        Bruker bruker = new Bruker();
        NorskIdent ident = new NorskIdent();
        ident.setIdent(testPerson.getFødselsnummer().asValue());
        PersonIdent pi = new PersonIdent();
        pi.setIdent(ident);
        bruker.setAktoer(pi);
        Personnavn personNavn = new Personnavn();
        personNavn.setSammensattNavn(testPerson.getNavn());
        bruker.setPersonnavn(personNavn);
        return bruker;
    }
}
