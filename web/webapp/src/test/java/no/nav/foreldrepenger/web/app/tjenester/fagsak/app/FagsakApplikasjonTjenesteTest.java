package no.nav.foreldrepenger.web.app.tjenester.fagsak.app;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerFagsakKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.ResourceLink;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakMedPersonDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.PersonDto;
import no.nav.fplos.person.PersonTjeneste;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClientFeil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FagsakApplikasjonTjenesteTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("12345678901");
    private static final AktørId AKTØR_ID = new AktørId("1");
    private static final String SAKSNUMMER  = "123";
    private static final boolean ER_KVINNE = true;

    private FagsakApplikasjonTjeneste fagsakTjeneste;
    private ForeldrepengerFagsakKlient fagsakKlient;
    private PersonTjeneste personTjeneste;

    @BeforeEach
    public void oppsett() {
        personTjeneste = mock(PersonTjeneste.class);
        fagsakKlient = mock(ForeldrepengerFagsakKlient.class);
        fagsakTjeneste = new FagsakApplikasjonTjeneste(fagsakKlient);
    }

    @Test
    public void skal_hente_saker_på_fnr() {
        PersonDto personinfoSoker = new PersonDto("TEST", 20, FNR.asValue(), ER_KVINNE, "", null);
        FagsakDto fagsakDto = new FagsakDto(Long.valueOf(FNR.asValue()), FagsakYtelseType.FORELDREPENGER,
                FagsakStatus.OPPRETTET, LocalDate.of(2017, Month.FEBRUARY, 1), Collections.emptyList());

        when(fagsakKlient.finnFagsaker(FNR.asValue())).thenReturn(Collections.singletonList(fagsakDto));

        LocalDate fødselsdatoBarn = LocalDate.of(2017, Month.FEBRUARY, 1);

        // Act
        List<FagsakMedPersonDto> fagsakDtos = fagsakTjeneste.hentSaker(FNR.asValue());

        // Assert
        assertThat(fagsakDtos.isEmpty()).isFalse();
        assertThat(fagsakDtos).hasSize(1);
        var fagsakMedPersonDto = fagsakDtos.get(0);
        assertThat(fagsakMedPersonDto.getSaksnummer()).isEqualTo(fagsakDto.getSaksnummer());
        assertThat(fagsakMedPersonDto.getSakstype()).isEqualTo(fagsakDto.getSakstype());
        assertThat(fagsakMedPersonDto.getStatus()).isEqualTo(fagsakDto.getStatus());
        assertThat(fagsakMedPersonDto.getBarnFodt()).isEqualTo(fødselsdatoBarn);
    }

    @Test
    public void skal_hente_saker_på_saksreferanse() {
        PersonDto personDto = new PersonDto("TEST", 20, FNR.asValue(), ER_KVINNE, "", null);
        ResourceLink rel = ResourceLink.get("test-uri", "sak-bruker", "aktørIdDtoObjekt");

        List<FagsakDto> fagsakDtos = new ArrayList<>();
        FagsakDto fagsakDto = new FagsakDto(Long.valueOf(SAKSNUMMER), FagsakYtelseType.FORELDREPENGER,
                FagsakStatus.UNDER_BEHANDLING, LocalDate.now(), List.of(rel));
        fagsakDtos.add(fagsakDto);
        when(fagsakKlient.finnFagsaker(SAKSNUMMER)).thenReturn(fagsakDtos);
        when(fagsakKlient.get(any(), any())).thenReturn(personDto);

        // Act
        List<FagsakMedPersonDto> resultFagsakDtos = fagsakTjeneste.hentSaker(SAKSNUMMER);

        // Assert
        assertThat(resultFagsakDtos.isEmpty()).isFalse();
        assertThat(resultFagsakDtos).hasSize(1);
        assertThat(resultFagsakDtos.get(0).getPerson()).isEqualTo(personDto);
    }

    @Test
    public void skal_returnere_tomt_view_dersom_søkestreng_ikke_er_gyldig_fnr_eller_saksnr() {
        var fagsakDtos = fagsakTjeneste.hentSaker("ugyldig_søkestreng");
        assertThat(fagsakDtos.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_ukjent_fnr() {
        String ukjentFødselsnummer = "00000000000";
        when(personTjeneste.hentPerson(any(AktørId.class))).thenReturn(Optional.empty());
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
        var person = new Person.Builder()
                .medAktørId(new AktørId(1234L))
                .medFnr(FNR)
                .medNavn("Test testen")
                .build();
        when(personTjeneste.hentPerson(any(Fødselsnummer.class))).thenReturn(Optional.of(person));
        when(fagsakKlient.finnFagsaker(any())).thenThrow(manglerTilgangException());
        var view = fagsakTjeneste.hentSaker(FNR.asValue());
        assertThat(view).isEmpty();
    }

    private static ManglerTilgangException manglerTilgangException() {
        var fpsak403 = OidcRestClientFeil.FACTORY.manglerTilgang("");
        return new ManglerTilgangException(fpsak403);
    }
}
