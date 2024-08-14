package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;

import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.SaksbehandlerOgGruppeDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto.SaksbehandlerGruppeDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto.SaksbehandlerGruppeNavneEndringRequestDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto.SaksbehandlerGruppeSletteRequestDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDtoTjeneste;

import no.nav.vedtak.felles.jpa.TomtResultatException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class AvdelingslederSaksbehandlerRestTjenesteTest {

    private static final AvdelingEnhetDto avdelingDto = new AvdelingEnhetDto("4817");
    private static final SaksbehandlerBrukerIdentDto brukerIdentDto = new SaksbehandlerBrukerIdentDto("Z999999");
    private static final SaksbehandlerDto saksbehandlerDto = new SaksbehandlerDto(brukerIdentDto, "Navnesen, Navn", "NAV Drammen");
    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;
    @Mock
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    private AvdelingslederSaksbehandlerRestTjeneste restTjeneste;

    @Mock
    private OppgaveRepository oppgaveRepository;

    @BeforeEach
    public void setUp(EntityManager entityManager) {
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        avdelingslederSaksbehandlerTjeneste = new AvdelingslederSaksbehandlerTjeneste(oppgaveRepository, organisasjonRepository);
        restTjeneste = new AvdelingslederSaksbehandlerRestTjeneste(avdelingslederSaksbehandlerTjeneste, saksbehandlerDtoTjeneste);
    }

    @Test
    void kan_hente_lagrede_grupper() {
        var nyGruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        var hentetGruppe = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        assertThat(nyGruppe).isNotNull().isInstanceOf(SaksbehandlerGruppeDto.class);
        assertThat(hentetGruppe.saksbehandlerGrupper().get(0)).isEqualTo(nyGruppe);
    }

    @Test
    void kan_slette_gruppe() {
        var gruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        restTjeneste.slettSaksbehandlerGruppe(new SaksbehandlerGruppeSletteRequestDto(gruppe.gruppeId(), avdelingDto));
        var etterSletting = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        assertThat(etterSletting.saksbehandlerGrupper()).isEmpty();
    }

    @Test
    void kan_legge_saksbehandlere_til_gruppe() {
        setupMockForMappingAvSaksbehandlerDto();

        var gruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        restTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(brukerIdentDto, avdelingDto));
        restTjeneste.leggSaksbehandlerTilGruppe(new SaksbehandlerOgGruppeDto(brukerIdentDto, avdelingDto, (int) gruppe.gruppeId()));
        var hentetGrupper = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);

        assertThat(hentetGrupper.saksbehandlerGrupper()).hasSize(1);
        var saksbehandlere = hentetGrupper.saksbehandlerGrupper().get(0).saksbehandlere();
        assertThat(saksbehandlere).hasSize(1);
        assertThat(saksbehandlere.get(0).brukerIdent().getVerdi()).isEqualTo("Z999999");
    }

    @Test
    void kan_fjerne_saksbehandlere_fra_gruppe() {
        setupMockForMappingAvSaksbehandlerDto();
        var gruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        restTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(brukerIdentDto, avdelingDto));
        restTjeneste.leggSaksbehandlerTilGruppe(new SaksbehandlerOgGruppeDto(brukerIdentDto, avdelingDto, gruppe.gruppeId()));
        var hentetGrupper = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        assertThat(hentetGrupper.saksbehandlerGrupper().get(0).saksbehandlere()).hasSize(1);

        restTjeneste.fjernSaksbehandlerFraGruppe(new SaksbehandlerOgGruppeDto(brukerIdentDto, avdelingDto, gruppe.gruppeId()));

        var etterSletting = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        assertThat(etterSletting.saksbehandlerGrupper().get(0).saksbehandlere()).isEmpty();
    }

    @Test
    void kan_gi_grupper_nytt_navn() {
        var gruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        assertThat(gruppe.gruppeNavn()).isNotEqualTo("Nytt navn");
        restTjeneste.endreSaksbehandlerGruppe(new SaksbehandlerGruppeNavneEndringRequestDto(gruppe.gruppeId(), "Nytt navn", avdelingDto));
        var hentetGrupper = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        var oppdatertGruppe = hentetGrupper.saksbehandlerGrupper().get(0);
        assertThat(oppdatertGruppe.gruppeNavn()).isEqualTo("Nytt navn");
    }

    @Test
    void skal_gi_feilmelding_når_gruppe_ikke_finnes() {
        var dto = new SaksbehandlerGruppeSletteRequestDto(1, avdelingDto);
        assertThatThrownBy(() -> restTjeneste.slettSaksbehandlerGruppe(dto)).isInstanceOf(TomtResultatException.class)
            .extracting(Throwable::getMessage)
            .matches(s -> s.contains("Fant ikke gruppe " + dto.gruppeId() + " for avdeling " + avdelingDto.getAvdelingEnhet()));
    }

    @Test
    void skal_håndtere_at_saksbehandler_ikke_er_tilknyttet_gruppe() {
        var gruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        var dto = new SaksbehandlerOgGruppeDto(brukerIdentDto, avdelingDto, gruppe.gruppeId());
        assertThatNoException().isThrownBy(() -> restTjeneste.fjernSaksbehandlerFraGruppe(dto));
    }

    @Test
    void skal_kunne_fjerne_saksbehandler_fra_individuelle_grupper() {
        setupMockForMappingAvSaksbehandlerDto();
        var førsteGruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        restTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(brukerIdentDto, avdelingDto));
        restTjeneste.leggSaksbehandlerTilGruppe(new SaksbehandlerOgGruppeDto(brukerIdentDto, avdelingDto, førsteGruppe.gruppeId()));
        var andreGruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        var saksbehandlerOgGruppeDto = new SaksbehandlerOgGruppeDto(brukerIdentDto, avdelingDto, andreGruppe.gruppeId());
        restTjeneste.leggSaksbehandlerTilGruppe(saksbehandlerOgGruppeDto);
        restTjeneste.fjernSaksbehandlerFraGruppe(saksbehandlerOgGruppeDto);

        var hentetGrupper = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        var hentetGrupperListe = hentetGrupper.saksbehandlerGrupper();
        assertThat(hentetGrupperListe).hasSize(2);
        for (var res : hentetGrupperListe) {
            if (Objects.equals(res.gruppeId(), førsteGruppe.gruppeId())) {
                assertThat(res.saksbehandlere()).hasSize(1);
            } else if (Objects.equals(res.gruppeId(), andreGruppe.gruppeId())) {
                assertThat(res.saksbehandlere()).isEmpty();
            }
            else {
                fail("Ukjent gruppe");
            }
        }
    }

    private void setupMockForMappingAvSaksbehandlerDto() {
        when(saksbehandlerDtoTjeneste.lagKjentOgUkjentSaksbehandler(argThat(sb -> sb.getSaksbehandlerIdent().equals("Z999999"))))
            .thenReturn(saksbehandlerDto);
    }

}
