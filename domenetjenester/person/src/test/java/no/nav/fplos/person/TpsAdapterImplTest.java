package no.nav.fplos.person;

import no.nav.foreldrepenger.loslager.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.integrasjon.aktør.klient.AktørConsumerMedCache;
import no.nav.vedtak.felles.integrasjon.aktør.klient.DetFinnesFlereAktørerMedSammePersonIdentException;
import no.nav.vedtak.felles.integrasjon.person.PersonConsumer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TpsAdapterImplTest {

    private TpsAdapterImpl tpsAdapterImpl;

    private AktørConsumerMedCache aktørConsumerMock = Mockito.mock(AktørConsumerMedCache.class);
    private PersonConsumer personProxyServiceMock = Mockito.mock(PersonConsumer.class);

    private final AktørId AKTØRID = new AktørId(1337L);
    private final PersonIdent FNR = new PersonIdent("31018143212");

    @Before
    public void setup() {
        tpsAdapterImpl = new TpsAdapterImpl(aktørConsumerMock, personProxyServiceMock, new TpsOversetter());
    }

    @Test
    public void test_hentAktørIdForPersonIdent_normal(){
        Mockito.when(aktørConsumerMock.hentAktørIdForPersonIdent("125343412")).thenReturn(Optional.of(AKTØRID.getId()));
        AktørId aktørId = (tpsAdapterImpl.hentAktørIdForPersonIdent(new PersonIdent("125343412"))).orElse(new AktørId(3L));
        assertThat(aktørId).isEqualTo(AKTØRID);

    }

    @Test
    public void test_hentAktørIdForPersonIdent_ikkeFunnet(){
        Mockito.when(aktørConsumerMock.hentAktørIdForPersonIdent("125343412")).thenReturn(Optional.empty());

        Optional<AktørId> optAktørId = tpsAdapterImpl.hentAktørIdForPersonIdent(new PersonIdent("125343412"));
        assertThat(optAktørId).isEmpty();
    }

    @Test
    public void skal_returnere_tom_når_det_finnes_flere_enn_en_aktør_på_samme_ident___kan_skje_ved_dødfødsler(){
        DetFinnesFlereAktørerMedSammePersonIdentException exception = new DetFinnesFlereAktørerMedSammePersonIdentException(Mockito.mock(Feil.class));
        String fnr2 = "125343412";
        Mockito.when(aktørConsumerMock.hentAktørIdForPersonIdent(fnr2))
            .thenThrow(exception);

        Optional<AktørId> optAktørId = tpsAdapterImpl.hentAktørIdForPersonIdent(new PersonIdent(fnr2));
        assertThat(optAktørId).isEmpty();
    }

    @Test
    public void test_hentIdentForAktørId_normal(){
        Mockito.when(aktørConsumerMock.hentPersonIdentForAktørId("1")).thenReturn(Optional.of("1337"));
        PersonIdent ident = tpsAdapterImpl.hentIdentForAktørId(new AktørId(1L)).orElse(new PersonIdent("3254"));
        assertThat(ident).isEqualTo(new PersonIdent("1337"));
    }

    @Test
    public void test_hentIdentForAktørId_ikkeFunnet(){
        Mockito.when(aktørConsumerMock.hentPersonIdentForAktørId("1")).thenReturn(Optional.empty());
        Optional<PersonIdent> optIdent = tpsAdapterImpl.hentIdentForAktørId(new AktørId(1L));
        assertThat(optIdent).isNotPresent();
    }

    @Test
    public void test_hentKjerneinformasjon_normal() throws Exception {
        PersonIdent fnr = new PersonIdent("31018143212");
        String navn = "John Doe";
        LocalDate fødselsdato = LocalDate.of(1343, 12, 12);
        String kjønn = NavBrukerKjønn.K.name();

        HentPersonResponse response = new HentPersonResponse();
        Bruker person = new Bruker();
        response.setPerson(person);
        Mockito.when(personProxyServiceMock.hentPersonResponse(Mockito.any())).thenReturn(response);

        TpsOversetter tpsOversetterMock = Mockito.mock(TpsOversetter.class);
        TpsPersonDto personinfo0 = new TpsPersonDto.Builder()
            .medFnr(fnr)
            .medNavn(navn)
            .medFødselsdato(fødselsdato)
            .medNavBrukerKjønn(kjønn)
            .medAktørId(AKTØRID)
            .build();

        Mockito.when(tpsOversetterMock.tilBrukerInfo(any(), eq(person))).thenReturn(personinfo0);
        tpsAdapterImpl = new TpsAdapterImpl(aktørConsumerMock, personProxyServiceMock, tpsOversetterMock);

        TpsPersonDto personinfo = tpsAdapterImpl.hentKjerneinformasjon(fnr, AKTØRID);
        assertNotNull(personinfo);
        assertThat(personinfo.getAktørId()).isEqualTo(AKTØRID);
        assertThat(personinfo.getPersonIdent()).isEqualTo(fnr);
        assertThat(personinfo.getNavn()).isEqualTo(navn);
        assertThat(personinfo.getFødselsdato()).isEqualTo(fødselsdato);
    }

    @Test(expected = TekniskException.class)
    public void skal_få_exception_når_tjenesten_ikke_kan_finne_personen() throws Exception {
        Mockito.when(personProxyServiceMock.hentPersonResponse(Mockito.any()))
            .thenThrow(new HentPersonPersonIkkeFunnet(null, null));

        tpsAdapterImpl.hentKjerneinformasjon(FNR, AKTØRID);
    }

    // TODO: Slå på når PFP-1763 er ferdig. tpsAdaptet skal returnere exception og ikke null som den gjør nå
    @Ignore
    @Test(expected = ManglerTilgangException.class)
    public void skal_få_exception_når_tjenesten_ikke_kan_aksesseres_pga_manglende_tilgang() throws Exception {
        when(personProxyServiceMock.hentPersonResponse(any(HentPersonRequest.class)))
            .thenThrow(new HentPersonSikkerhetsbegrensning(null, null));

        tpsAdapterImpl.hentKjerneinformasjon(FNR, AKTØRID);
    }
}
