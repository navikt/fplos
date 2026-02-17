package no.nav.foreldrepenger.los.avdelingsleder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteLagreDto;

@ExtendWith(JpaExtension.class)
class AvdelingslederTjenesteTest {

    private OppgaveRepository oppgaveRepository;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private EntityManager entityManager;

    @BeforeEach
    void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
    }

    @Test
    void testLagNyListe() {
        avdelingslederTjeneste.lagNyOppgaveFiltrering(Avdeling.AVDELING_DRAMMEN_ENHET);
        var oppgaveFiltreringer = oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetEnhet(Avdeling.AVDELING_DRAMMEN_ENHET);
        assertThat(oppgaveFiltreringer).isNotNull();
        assertThat(oppgaveFiltreringer.getFirst().getId()).isNotNull();
        assertThat(oppgaveFiltreringer.getFirst().getNavn()).isEqualTo("Ny liste");
        assertThat(oppgaveFiltreringer.getFirst().getAvdeling()).isEqualTo(avdelingDrammen());
    }

    private Avdeling avdelingDrammen() {
        return DBTestUtil.hentAlle(entityManager, Avdeling.class)
                         .stream()
                         .filter(avdeling -> Avdeling.AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet()))
                         .findFirst()
                         .orElseThrow();
    }


    @Test
    void testSlettListe() throws IllegalArgumentException {
        var liste = new OppgaveFiltrering();
        liste.setNavn("Test");
        liste.setAvdeling(avdelingDrammen());
        liste.setSortering(KøSortering.BEHANDLINGSFRIST);
        persistAndFlush(liste);
        avdelingslederTjeneste.slettOppgaveFiltrering(liste);
        entityManager.flush();
        assertThat(oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetEnhet(Avdeling.AVDELING_DRAMMEN_ENHET)).isEmpty();
    }

    @Test
    void test_samlet() {
        var liste = new OppgaveFiltrering();
        liste.setNavn("Test");
        liste.setAvdeling(avdelingDrammen());
        liste.setSortering(KøSortering.BEHANDLINGSFRIST);
        persistAndFlush(liste);


        // Act
        var saksliste = new SakslisteLagreDto(
            liste.getAvdeling().getAvdelingEnhet(),
            liste.getId(),
            liste.getNavn(),
            new SakslisteLagreDto.SorteringDto(KøSortering.BELØP, Periodefilter.FAST_PERIODE, 100L, 200L, null, null),
            Set.of(BehandlingType.TILBAKEBETALING),
            Set.of(FagsakYtelseType.ENGANGSTØNAD, FagsakYtelseType.FORELDREPENGER),
            new SakslisteLagreDto.AndreKriterieDto(Set.of(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD), Set.of(AndreKriterierType.TERMINBEKREFTELSE))
        );
        avdelingslederTjeneste.endreEksistrendeOppgaveFilter(saksliste);

        // Assert
        var oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(saksliste.sakslisteId());
        assertThat(oppgaveFiltrering).isPresent();
        assertThat(oppgaveFiltrering.get().getNavn()).isEqualTo(saksliste.navn());
        assertThat(oppgaveFiltrering.get().getSortering()).isEqualTo(saksliste.sortering().sorteringType());
        assertThat(oppgaveFiltrering.get().getPeriodefilter()).isEqualTo(saksliste.sortering().periodefilter());
        assertThat(oppgaveFiltrering.get().getFra()).isEqualTo(saksliste.sortering().fra());
        assertThat(oppgaveFiltrering.get().getTil()).isEqualTo(saksliste.sortering().til());
        assertThat(oppgaveFiltrering.get().getBehandlingTyper()).containsExactlyInAnyOrderElementsOf(saksliste.behandlingTyper());
        assertThat(oppgaveFiltrering.get().getFagsakYtelseTyper()).containsExactlyInAnyOrderElementsOf(saksliste.fagsakYtelseTyper());
        assertThat(oppgaveFiltrering.get().getFiltreringAndreKriterierTyper()).hasSize(saksliste.andreKriterie().inkluder().size() + saksliste.andreKriterie().ekskluder().size());
        assertThat(oppgaveFiltrering.get().getFiltreringAndreKriterierTyper())
            .filteredOn(FiltreringAndreKriterierType::isInkluder)
            .extracting(FiltreringAndreKriterierType::getAndreKriterierType)
            .containsExactlyInAnyOrderElementsOf(saksliste.andreKriterie().inkluder());
        assertThat(oppgaveFiltrering.get().getFiltreringAndreKriterierTyper())
            .filteredOn(filtreringAndreKriterierType -> !filtreringAndreKriterierType.isInkluder())
            .extracting(FiltreringAndreKriterierType::getAndreKriterierType)
            .containsExactlyInAnyOrderElementsOf(saksliste.andreKriterie().ekskluder());
    }

    @Test
    void testHentAvdelinger() {
        var avdelinger = avdelingslederTjeneste.hentAvdelinger();
        Assertions.assertThat(avdelinger).isNotEmpty().hasSizeGreaterThan(1);
    }

    private void persistAndFlush(OppgaveFiltrering oppgaveFiltrering) {
        entityManager.persist(oppgaveFiltrering);
        entityManager.flush();
    }
}
