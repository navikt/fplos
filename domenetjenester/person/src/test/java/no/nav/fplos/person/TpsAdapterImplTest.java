package no.nav.fplos.person;

import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.vedtak.felles.integrasjon.aktør.klient.AktørConsumerMedCache;
import no.nav.vedtak.felles.integrasjon.person.PersonConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class TpsAdapterImplTest {

    private TpsAdapterImpl tpsAdapter;

    private final AktørConsumerMedCache aktørConsumerMock = Mockito.mock(AktørConsumerMedCache.class);
    private final PersonConsumer personProxyServiceMock = Mockito.mock(PersonConsumer.class);

    private static final Person PERSON = FiktivTestPerson.nyPerson();
    private static final Person UKJENT_PERSON = FiktivTestPerson.nyPerson();

    @BeforeEach
    public void setup() {
        tpsAdapter = new TpsAdapterImpl(aktørConsumerMock, personProxyServiceMock);
    }

    @Test
    public void hentPerson_funnet() throws Exception {
        Mockito.when(personProxyServiceMock.hentPersonResponse(Mockito.any())).thenReturn(tpsPersonRespons());
        Mockito.when(aktørConsumerMock.hentPersonIdentForAktørId(any()))
                .thenReturn(Optional.of(PERSON.getFødselsnummer().asValue()));
        Person personFraTps = tpsAdapter.hentPerson(PERSON.getAktørId()).orElseThrow();
        assertEquals(personFraTps, PERSON);
    }

    @Test
    public void skal_få_empty_når_hentPerson_ikke_kan_finne_personen() throws Exception {
        Mockito.when(personProxyServiceMock.hentPersonResponse(Mockito.any()))
                .thenThrow(new HentPersonPersonIkkeFunnet(null, null));
        Optional<Person> OptionalPersonFraTps = tpsAdapter.hentPerson(UKJENT_PERSON.getAktørId());
        assertTrue(OptionalPersonFraTps.isEmpty());
    }

    private static HentPersonResponse tpsPersonRespons() {
        // get ready, set
        HentPersonResponse response = new HentPersonResponse();
        Bruker tpsPerson = new Bruker();
        Personnavn sammensattNavn = new Personnavn();
        sammensattNavn.setSammensattNavn(PERSON.getNavn());
        tpsPerson.setPersonnavn(sammensattNavn);
        PersonIdent aktør = new PersonIdent();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(PERSON.getFødselsnummer().asValue());
        aktør.setIdent(norskIdent);
        tpsPerson.setAktoer(aktør);
        response.setPerson(tpsPerson);
        // phew
        return response;
    }
}
