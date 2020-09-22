package no.nav.fplos.person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
public class TpsOversetterTest {

    @Mock
    private Bruker bruker;

    private TpsOversetter tpsOversetter;


    @BeforeEach
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
