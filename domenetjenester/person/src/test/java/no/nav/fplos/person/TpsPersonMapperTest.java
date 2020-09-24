package no.nav.fplos.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn;

@ExtendWith(MockitoExtension.class)
public class TpsPersonMapperTest {

    @Mock
    private Bruker bruker;


    @BeforeEach
    public void oppsett() {
        NorskIdent ident = new NorskIdent();
        ident.setIdent("123");
        PersonIdent pi = new PersonIdent();
        pi.setIdent(ident);

        when(bruker.getAktoer()).thenReturn(pi);

        Personnavn personnavn = new Personnavn();
        personnavn.setSammensattNavn("Ole Olsen");
        when(bruker.getPersonnavn()).thenReturn(personnavn);
    }

    @Test
    public void skal_oversette_til_person() {
        Person person = TpsPersonMapper.tilPerson(new AktørId(123L), bruker);

        assertThat(person.getNavn()).isEqualTo("Ole Olsen");
        assertThat(person.getFødselsnummer().asValue()).isEqualTo("123");
    }
}
