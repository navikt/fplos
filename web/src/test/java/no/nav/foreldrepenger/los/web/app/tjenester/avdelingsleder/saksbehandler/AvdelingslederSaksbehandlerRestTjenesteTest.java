package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.extensions.JpaExtension;

import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.SaksbehandlerOgGruppeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;

import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class AvdelingslederSaksbehandlerRestTjenesteTest {

    private static final AvdelingEnhetDto avdelingDto = new AvdelingEnhetDto("4817");
    private static final SaksbehandlerBrukerIdentDto brukerIdentDto = new SaksbehandlerBrukerIdentDto("Z999999");
    private static final SaksbehandlerDto saksbehandlerDto = new SaksbehandlerDto(brukerIdentDto, "Navn Navnesen");
    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;
    @Mock
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    private AvdelingslederSaksbehandlerRestTjeneste restTjeneste;

    @Mock
    private OppgaveRepository oppgaveRepository;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
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
        restTjeneste.slettSaksbehandlerGruppe(new SaksbehandlerGruppeSletteRequestDto((int) gruppe.gruppeId(), avdelingDto));
        var etterSletting = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        assertThat(etterSletting.saksbehandlerGrupper()).hasSize(0);
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
        assertThat(saksbehandlere.get(0).getBrukerIdent()).isEqualTo("Z999999");
    }

    @Test
    void kan_fjerne_saksbehandlere_fra_gruppe() {
        setupMockForMappingAvSaksbehandlerDto();
        var gruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        restTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(brukerIdentDto, avdelingDto));
        restTjeneste.leggSaksbehandlerTilGruppe(new SaksbehandlerOgGruppeDto(brukerIdentDto, avdelingDto, (int) gruppe.gruppeId()));
        var hentetGrupper = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        assertThat(hentetGrupper.saksbehandlerGrupper().get(0).saksbehandlere()).hasSize(1);

        restTjeneste.fjernSaksbehandlerFraGruppe(new SaksbehandlerOgGruppeDto(brukerIdentDto, avdelingDto, (int) gruppe.gruppeId()));
        var etterSletting = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        assertThat(etterSletting.saksbehandlerGrupper().get(0).saksbehandlere()).hasSize(0);
    }

    @Test
    void kan_gi_grupper_nytt_navn() {
        var gruppe = restTjeneste.opprettSaksbehandlerGruppe(avdelingDto);
        assertThat(gruppe.gruppeNavn()).isNotEqualTo("Nytt navn");
        restTjeneste.endreSaksbehandlerGruppe(new SaksbehandlerGruppeNavneEndringDto((int) gruppe.gruppeId(), "Nytt navn", avdelingDto));
        entityManager.clear(); // simulerer ny transaksjon
        var hentetGrupper = restTjeneste.hentSaksbehandlerGrupper(avdelingDto);
        var oppdatertGruppe = hentetGrupper.saksbehandlerGrupper().get(0);
        assertThat(oppdatertGruppe.gruppeNavn()).isEqualTo("Nytt navn");
    }

    private void setupMockForMappingAvSaksbehandlerDto() {
        when(saksbehandlerDtoTjeneste.lagKjentOgUkjentSaksbehandlerMedAvdelingerDto(argThat(sb -> sb.getSaksbehandlerIdent().equals("Z999999"))))
            .thenReturn(new SaksbehandlerMedAvdelingerDto(saksbehandlerDto, singletonList(avdelingDto.getAvdelingEnhet())));
    }



}
