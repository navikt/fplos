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

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.domene.typer.aktør.Fødselsnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerFagsaker;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakYtelseTypeDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.PersonDto;
import no.nav.foreldrepenger.los.klient.person.PersonTjeneste;
import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.vedtak.exception.ManglerTilgangException;

@ExtendWith(MockitoExtension.class)
public class FagsakApplikasjonTjenesteTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("12345678901");
    private static final String SAKSNUMMER = "123";

    private FagsakApplikasjonTjeneste fagsakTjeneste;
    private ForeldrepengerFagsaker fagsakKlient;
    private PersonTjeneste personTjeneste;

    @BeforeEach
    public void oppsett() {
        personTjeneste = mock(PersonTjeneste.class);
        fagsakKlient = mock(ForeldrepengerFagsaker.class);
        fagsakTjeneste = new FagsakApplikasjonTjeneste(fagsakKlient, personTjeneste);
    }

    @Test
    public void skal_hente_saker_på_fnr() {
        var fagsakDto = new FagsakDto(AktørId.dummy().getId(), AktørId.dummy().getId(), FNR.value(), FagsakYtelseTypeDto.FORELDREPENGER,
                FagsakStatus.OPPRETTET, LocalDate.of(2017, Month.FEBRUARY, 1), LocalDate.of(2017, Month.FEBRUARY, 1));
        var personDto = new Person.Builder().medNavn("TEST")
                .medFødselsdato(LocalDate.now().minusYears(20))
                .medFnr(FNR)
                .medKjønn(NavBrukerKjønn.K)
                .build();
        when(fagsakKlient.finnFagsaker(FNR.value())).thenReturn(Collections.singletonList(fagsakDto));
        when(personTjeneste.hentPerson(any(), any())).thenReturn(Optional.of(personDto));
        var fødselsdatoBarn = LocalDate.of(2017, Month.FEBRUARY, 1);

        // Act
        var fagsakDtos = fagsakTjeneste.hentSaker(FNR.value());

        // Assert
        assertThat(fagsakDtos.isEmpty()).isFalse();
        assertThat(fagsakDtos).hasSize(1);
        var fagsakMedPersonDto = fagsakDtos.get(0);
        assertThat(fagsakMedPersonDto.saksnummerString()).isEqualTo(fagsakDto.saksnummer());
        assertThat(fagsakMedPersonDto.fagsakYtelseType().getKode()).isEqualTo(
                fagsakDto.fagsakYtelseType().getKode());
        assertThat(fagsakMedPersonDto.status()).isEqualTo(fagsakDto.status());
        assertThat(fagsakMedPersonDto.barnFødt()).isEqualTo(fødselsdatoBarn);
    }

    @Test
    public void skal_hente_saker_på_saksreferanse() {
        var personDto = new Person.Builder().medNavn("TEST")
                .medFødselsdato(LocalDate.now().minusYears(20))
                .medFnr(FNR)
                .medKjønn(NavBrukerKjønn.K)
                .build();

        List<FagsakDto> fagsakDtos = new ArrayList<>();
        var fagsakDto = new FagsakDto(AktørId.dummy().getId(), AktørId.dummy().getId(), SAKSNUMMER, FagsakYtelseTypeDto.FORELDREPENGER,
                FagsakStatus.UNDER_BEHANDLING, LocalDate.now(), LocalDate.now());
        fagsakDtos.add(fagsakDto);
        when(fagsakKlient.finnFagsaker(SAKSNUMMER)).thenReturn(fagsakDtos);
        when(personTjeneste.hentPerson(any(), any())).thenReturn(Optional.of(personDto));

        // Act
        var resultFagsakDtos = fagsakTjeneste.hentSaker(SAKSNUMMER);

        // Assert
        assertThat(resultFagsakDtos.isEmpty()).isFalse();
        assertThat(resultFagsakDtos).hasSize(1);
        assertThat(resultFagsakDtos.get(0).person()).isEqualTo(new PersonDto(personDto));
    }

    @Test
    public void skal_returnere_tomt_view_dersom_søkestreng_ikke_er_gyldig_fnr_eller_saksnr() {
        var fagsakDtos = fagsakTjeneste.hentSaker("ugyldig_søkestreng");
        assertThat(fagsakDtos.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_ukjent_fnr() {
        var ukjentFødselsnummer = "00000000000";
        var view = fagsakTjeneste.hentSaker(ukjentFødselsnummer);
        assertThat(view.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_ukjent_saksnr() {
        var manglerTilgangException = manglerTilgangException();
        when(fagsakKlient.finnFagsaker(any(String.class))).thenThrow(manglerTilgangException);

        var view = fagsakTjeneste.hentSaker(SAKSNUMMER);
        assertThat(view.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_403_fra_fpsak_ved_fnrsøk() {
        when(fagsakKlient.finnFagsaker(any())).thenThrow(manglerTilgangException());
        var view = fagsakTjeneste.hentSaker(FNR.value());
        assertThat(view).isEmpty();
    }

    private static ManglerTilgangException manglerTilgangException() {
        return new ManglerTilgangException("F-468815",
                String.format("Mangler tilgang. Fikk http-kode 403 fra server [%s]", URI.create("http://testuri")));
    }
}
