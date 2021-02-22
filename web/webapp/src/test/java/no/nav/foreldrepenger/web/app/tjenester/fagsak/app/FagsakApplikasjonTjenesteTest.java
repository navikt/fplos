package no.nav.foreldrepenger.web.app.tjenester.fagsak.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerFagsakKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakMedPersonDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakYtelseTypeDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.PersonDto;
import no.nav.fplos.person.PersonTjeneste;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClientFeil;

@ExtendWith(MockitoExtension.class)
public class FagsakApplikasjonTjenesteTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("12345678901");
    private static final String SAKSNUMMER = "123";
    private static final boolean ER_KVINNE = true;

    private FagsakApplikasjonTjeneste fagsakTjeneste;
    private ForeldrepengerFagsakKlient fagsakKlient;
    private PersonTjeneste personTjeneste;

    @BeforeEach
    public void oppsett() {
        personTjeneste = mock(PersonTjeneste.class);
        fagsakKlient = mock(ForeldrepengerFagsakKlient.class);
        fagsakTjeneste = new FagsakApplikasjonTjeneste(fagsakKlient, personTjeneste);
    }

    @Test
    public void skal_hente_saker_på_fnr() {
        FagsakDto fagsakDto = new FagsakDto(AktørId.dummy().getId(), Long.valueOf(FNR.asValue()), FagsakYtelseTypeDto.FORELDREPENGER,
                FagsakStatus.OPPRETTET, LocalDate.of(2017, Month.FEBRUARY, 1));
        Person personDto = new Person.Builder().medNavn("TEST"). medFødselsdato(LocalDate.now().minusYears(20)).medFnr(FNR). medKjønn(NavBrukerKjønn.K).build();
        when(fagsakKlient.finnFagsaker(FNR.asValue())).thenReturn(Collections.singletonList(fagsakDto));
        when(personTjeneste.hentPerson(any())).thenReturn(Optional.of(personDto));
        LocalDate fødselsdatoBarn = LocalDate.of(2017, Month.FEBRUARY, 1);

        // Act
        List<FagsakMedPersonDto> fagsakDtos = fagsakTjeneste.hentSaker(FNR.asValue());

        // Assert
        assertThat(fagsakDtos.isEmpty()).isFalse();
        assertThat(fagsakDtos).hasSize(1);
        var fagsakMedPersonDto = fagsakDtos.get(0);
        assertThat(fagsakMedPersonDto.getSaksnummer()).isEqualTo(fagsakDto.getSaksnummer());
        assertThat(fagsakMedPersonDto.getSakstype().getKode()).isEqualTo(fagsakDto.getSakstype().getKode());
        assertThat(fagsakMedPersonDto.getStatus()).isEqualTo(fagsakDto.getStatus());
        assertThat(fagsakMedPersonDto.getBarnFodt()).isEqualTo(fødselsdatoBarn);
    }

    @Test
    public void skal_hente_saker_på_saksreferanse() {
        Person personDto = new Person.Builder().medNavn("TEST"). medFødselsdato(LocalDate.now().minusYears(20)).medFnr(FNR). medKjønn(NavBrukerKjønn.K).build();

        List<FagsakDto> fagsakDtos = new ArrayList<>();
        FagsakDto fagsakDto = new FagsakDto(AktørId.dummy().getId(), Long.valueOf(SAKSNUMMER), FagsakYtelseTypeDto.FORELDREPENGER,
                FagsakStatus.UNDER_BEHANDLING, LocalDate.now());
        fagsakDtos.add(fagsakDto);
        when(fagsakKlient.finnFagsaker(SAKSNUMMER)).thenReturn(fagsakDtos);
        when(personTjeneste.hentPerson(any())).thenReturn(Optional.of(personDto));

        // Act
        List<FagsakMedPersonDto> resultFagsakDtos = fagsakTjeneste.hentSaker(SAKSNUMMER);

        // Assert
        assertThat(resultFagsakDtos.isEmpty()).isFalse();
        assertThat(resultFagsakDtos).hasSize(1);
        assertThat(resultFagsakDtos.get(0).getPerson()).isEqualTo(new PersonDto(personDto));
    }

    @Test
    public void skal_returnere_tomt_view_dersom_søkestreng_ikke_er_gyldig_fnr_eller_saksnr() {
        var fagsakDtos = fagsakTjeneste.hentSaker("ugyldig_søkestreng");
        assertThat(fagsakDtos.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_ukjent_fnr() {
        String ukjentFødselsnummer = "00000000000";
        var view = fagsakTjeneste.hentSaker(ukjentFødselsnummer);
        assertThat(view.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_ukjent_saksnr() {
        ManglerTilgangException manglerTilgangException = manglerTilgangException();
        when(fagsakKlient.finnFagsaker(any(String.class))).thenThrow(manglerTilgangException);

        var view = fagsakTjeneste.hentSaker(SAKSNUMMER);
        assertThat(view.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_403_fra_fpsak_ved_fnrsøk() {
        when(fagsakKlient.finnFagsaker(any())).thenThrow(manglerTilgangException());
        var view = fagsakTjeneste.hentSaker(FNR.asValue());
        assertThat(view).isEmpty();
    }

    private static ManglerTilgangException manglerTilgangException() {
        var fpsak403 = OidcRestClientFeil.FACTORY.manglerTilgang(URI.create("http://testuri"));
        return new ManglerTilgangException(fpsak403);
    }
}
