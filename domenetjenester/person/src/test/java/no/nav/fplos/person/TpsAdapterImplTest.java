package no.nav.fplos.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
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
import static org.mockito.ArgumentMatchers.any;

public class TpsAdapterImplTest {

    private TpsAdapterImpl tpsAdapter;

    private final AktørConsumerMedCache aktørConsumerMock = Mockito.mock(AktørConsumerMedCache.class);
    private final PersonConsumer personProxyServiceMock = Mockito.mock(PersonConsumer.class);

    private final AktørId AKTØRID = new AktørId(1337L);

    @BeforeEach
    public void setup() {
        tpsAdapter = new TpsAdapterImpl(aktørConsumerMock, personProxyServiceMock);
    }

    @Test
    public void hentAktørForFødselsnummer() {
        Mockito.when(aktørConsumerMock.hentAktørIdForPersonIdent("12534341200")).thenReturn(Optional.of(AKTØRID.getId()));
        AktørId aktørId = tpsAdapter.hentAktørForFødselsnummer(new Fødselsnummer("12534341200")).orElse(new AktørId(3L));
        assertThat(aktørId).isEqualTo(AKTØRID);
    }

    @Test
    public void hentAktørForFødselsnummer_ikkeFunnet() {
        Mockito.when(aktørConsumerMock.hentAktørIdForPersonIdent("125343412")).thenReturn(Optional.empty());
        Optional<AktørId> optAktørId = tpsAdapter.hentAktørForFødselsnummer(new Fødselsnummer("12534341200"));
        assertThat(optAktørId).isEmpty();
    }

    @Test
    public void hentPerson_funnet() throws Exception {
        Fødselsnummer fnr = new Fødselsnummer("31018143212");
        String navn = "John Doe";

        // get ready, set
        HentPersonResponse response = new HentPersonResponse();
        Bruker tpsPerson = new Bruker();
        Personnavn sammensattNavn = new Personnavn();
        sammensattNavn.setSammensattNavn(navn);
        tpsPerson.setPersonnavn(sammensattNavn);
        PersonIdent aktør = new PersonIdent();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(fnr.asValue());
        aktør.setIdent(norskIdent);
        tpsPerson.setAktoer(aktør);
        response.setPerson(tpsPerson);
        // phew

        Mockito.when(personProxyServiceMock.hentPersonResponse(Mockito.any())).thenReturn(response);
        tpsAdapter = new TpsAdapterImpl(aktørConsumerMock, personProxyServiceMock);
        Mockito.when(aktørConsumerMock.hentPersonIdentForAktørId(any())).thenReturn(Optional.of(fnr.asValue()));

        // act
        Person person = tpsAdapter.hentPerson(AKTØRID).orElseThrow();

        assertThat(person).isNotNull();
        assertThat(person.getNavn()).isEqualTo(navn);
        assertThat(person.getFødselsnummer()).isEqualTo(fnr);
        assertThat(person.getAktørId()).isEqualTo(AKTØRID);
    }

    @Test
    public void skal_få_empty_når_hentPerson_ikke_kan_finne_personen() throws Exception {
        Mockito.when(personProxyServiceMock.hentPersonResponse(Mockito.any()))
            .thenThrow(new HentPersonPersonIkkeFunnet(null, null));
        Optional<Person> person = tpsAdapter.hentPerson(AKTØRID);
        assertThat(person.isEmpty()).isTrue();
    }
}
