package no.nav.foreldrepenger.web.app.tjenester.fagsak.app;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjenesteImpl;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.PersonDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.person.api.TpsTjeneste;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
public class FagsakApplikasjonTjenesteTest {

    private static final String FNR = "12345678901";
    private static final String FNR_BARN = "12345678902";
    private static final AktørId AKTØR_ID = new AktørId("1");
    private static final AktørId AKTØR_ID_BARN = new AktørId("2");
    private static final String SAKSNUMMER  = "123";
    private static final boolean ER_KVINNE = true;

    private FagsakApplikasjonTjeneste tjeneste;
    private ForeldrepengerBehandlingRestKlient klient;
    private TpsTjeneste tpsTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private AnsattTjeneste ansattTjeneste;

    @Before
    public void oppsett() {
        tpsTjeneste = mock(TpsTjeneste.class);
        klient = mock(ForeldrepengerBehandlingRestKlient.class);
        oppgaveTjeneste = mock(OppgaveTjeneste.class);
        ansattTjeneste = mock(AnsattTjeneste.class);

        tjeneste = new FagsakApplikasjonTjenesteImpl(tpsTjeneste, oppgaveTjeneste, ansattTjeneste, klient);
    }

    @Test
    public void skal_hente_saker_på_fnr() {
        // Arrange
        TpsPersonDto personinfo = new TpsPersonDto.Builder().medAktørId(AKTØR_ID)
                .medFnr(new PersonIdent(FNR))
                .medNavn("Test")
                .medFødselsdato(LocalDate.now())
                .medNavBrukerKjønn("M")
                .build();
        when(tpsTjeneste.hentBrukerForFnr(new PersonIdent(FNR))).thenReturn(Optional.of(personinfo));

        PersonDto personinfoSoker = new PersonDto("TEST", 20, FNR, ER_KVINNE, "", null);
        FagsakDto fagsakDto = new FagsakDto(Long.valueOf(FNR), FagsakYtelseType.FORELDREPENGER, FagsakStatus.OPPRETTET,
                personinfoSoker, LocalDateTime.now(), LocalDateTime.now(), LocalDate.of(2017, Month.FEBRUARY, 1));
        when(klient.getFagsakFraFnr(FNR)).thenReturn(Collections.singletonList(fagsakDto));

        LocalDate fødselsdatoBarn = LocalDate.of(2017, Month.FEBRUARY, 1);

        // Act
        List<FagsakDto> fagsakDtos = tjeneste.hentSaker(FNR);

        // Assert
        assertThat(fagsakDtos.isEmpty()).isFalse();
        assertThat(fagsakDtos).hasSize(1);
        FagsakDto resultFagsakDto = fagsakDtos.get(0);
        assertThat(resultFagsakDto).isEqualTo(fagsakDto);
        assertThat(fagsakDto.getBarnFodt()).isEqualTo(fødselsdatoBarn);
    }

    @Test
    public void skal_hente_saker_på_saksreferanse() {
        PersonDto personinfo = new PersonDto("TEST", 20, FNR, ER_KVINNE, "", null);

        List<FagsakDto> fagsakDtos = new ArrayList<>();
        FagsakDto fagsakDto = new FagsakDto(Long.valueOf(SAKSNUMMER), FagsakYtelseType.FORELDREPENGER, FagsakStatus.UNDER_BEHANDLING, personinfo, LocalDateTime.now(),
                LocalDateTime.now(), LocalDate.now());
        fagsakDtos.add(fagsakDto);
        when(klient.getFagsakFraSaksnummer(SAKSNUMMER)).thenReturn(fagsakDtos);


        // Act
        List<FagsakDto> resultFagsakDtos = tjeneste.hentSaker(SAKSNUMMER);

        // Assert
        assertThat(resultFagsakDtos.isEmpty()).isFalse();
        assertThat(resultFagsakDtos).hasSize(1);
        FagsakDto resultFagsakDto = resultFagsakDtos.get(0);
        assertThat(resultFagsakDto).isEqualTo(fagsakDto);
    }

    @Test
    public void skal_returnere_tomt_view_dersom_søkestreng_ikke_er_gyldig_fnr_eller_saksnr() {
        List<FagsakDto> fagsakDtos = tjeneste.hentSaker("ugyldig_søkestreng");

        assertThat(fagsakDtos.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_ukjent_fnr() {
        when(tpsTjeneste.hentBrukerForFnr(new PersonIdent(FNR))).thenReturn(Optional.empty());

        List<FagsakDto> view = tjeneste.hentSaker(FNR);

        assertThat(view.isEmpty()).isTrue();
    }

    @Test
    public void skal_returnere_tomt_view_ved_ukjent_saksnr() {
        List<FagsakDto> fagsakDtoListe = new ArrayList<>();
        when(klient.getFagsakFraSaksnummer(SAKSNUMMER)).thenReturn(fagsakDtoListe);

        List<FagsakDto> view = tjeneste.hentSaker(valueOf(SAKSNUMMER));

        assertThat(view.isEmpty()).isTrue();
    }
}
