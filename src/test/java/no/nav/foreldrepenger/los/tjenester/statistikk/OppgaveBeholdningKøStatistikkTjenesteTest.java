package no.nav.foreldrepenger.los.tjenester.statistikk;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRepository;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class OppgaveBeholdningKøStatistikkTjenesteTest {

    private final Oppgave førstegangOppgave = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave førstegangOppgave2 = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave klageOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.KLAGE).build();
    private final Oppgave innsynOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.INNSYN).build();

    private final Oppgave beslutterOppgave = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave beslutterOppgave2 = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave lukketOppgave = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medAktiv(false)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();

    private NøkkeltallRepository nøkkeltallRepository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        nøkkeltallRepository = new NøkkeltallRepository(entityManager);
        this.entityManager = entityManager;
    }

    private void leggInnEttSettMedOppgaver() {
        entityManager.persist(førstegangOppgave);
        entityManager.persist(førstegangOppgave2);
        entityManager.persist(klageOppgave);
        entityManager.persist(innsynOppgave);

        beslutterOppgave.leggTilOppgaveEgenskap(
            OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
        entityManager.persist(beslutterOppgave);

        beslutterOppgave2.leggTilOppgaveEgenskap(
            OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
        entityManager.persist(beslutterOppgave2);

        entityManager.persist(lukketOppgave);

        entityManager.flush();
    }

    @Test
    void hentAlleOppgaverForAvdelingTest() {
        leggInnEttSettMedOppgaver();
        var resultater = nøkkeltallRepository.hentAlleOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);

        assertThat(resultater).hasSize(4);
        assertThat(resultater.get(0).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(0).tilBehandling()).isFalse();
        assertThat(resultater.get(0).antall()).isEqualTo(2L);

        assertThat(resultater.get(1).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(1).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(1).tilBehandling()).isTrue();
        assertThat(resultater.get(1).antall()).isEqualTo(2L);

        assertThat(resultater.get(2).antall()).isEqualTo(1L);
        assertThat(resultater.get(3).antall()).isEqualTo(1L);
    }

    @Test
    void hentOppgaverPerFørsteStønadsdagMåned() {
        leggInnEttSettMedOppgaver();
        var idag = LocalDate.now();
        var idagPlusMnd = idag.plusMonths(1);
        var resultater = nøkkeltallRepository.hentOppgaverPerFørsteStønadsdagMåned(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(1);
        assertThat(resultater.get(0).førsteStønadsdag()).isEqualTo(idagPlusMnd.withDayOfMonth(1));
        assertThat(resultater.get(0).antall()).isEqualTo(4L);
    }

}
