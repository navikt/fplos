package no.nav.fplos.person;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Foedselsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kjoenn;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kjoennstyper;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn;
import no.nav.vedtak.felles.integrasjon.felles.ws.DateUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TpsOversetterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();

    @Mock
    private Bruker bruker;

    private TpsOversetter tpsOversetter;


    @Before
    public void oppsett() {
        NorskIdent ident = new NorskIdent();
        ident.setIdent("123");
        PersonIdent pi = new PersonIdent();
        pi.setIdent(ident);
        
        when(bruker.getAktoer()).thenReturn(pi);
        tpsOversetter = new TpsOversetter();

        Personnavn personnavn = new Personnavn();
        personnavn.setSammensattNavn("Ole Olsen");
        when(bruker.getPersonnavn()).thenReturn(personnavn);
    }

    @Test
    public void skal_oversette_til_brukerinfo() {
        // Arrange
        leggPåAndrePåkrevdeFelter();

        //Act
        TpsPersonDto personinfo = tpsOversetter.tilBrukerInfo(new AktørId(123L), bruker);

        //Assert
        assertThat(personinfo.erKvinne()).isTrue();
        assertThat(personinfo.getNavn()).isEqualTo("Ole Olsen");
        assertThat(personinfo.getFnr().getIdent()).isEqualTo("123");
    }


    private void leggPåAndrePåkrevdeFelter() {
        Kjoenn kjønn = new Kjoenn();
        Kjoennstyper kjønnstype = new Kjoennstyper();
        kjønnstype.setValue("K");
        kjønn.setKjoenn(kjønnstype);
        when(bruker.getKjoenn()).thenReturn(kjønn);

        Foedselsdato foedselsdato = new Foedselsdato();
        foedselsdato.setFoedselsdato(DateUtil.convertToXMLGregorianCalendar(LocalDate.now()));
        when(bruker.getFoedselsdato()).thenReturn(foedselsdato);
    }
}
