package no.nav.foreldrepenger.los.statistikk;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class StatistikkEnhetYtelseBehandlingTest {

    private static final String ANNEN_ENHET = "5555";

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
    private final Oppgave annenAvdeling = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlendeEnhet(ANNEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
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

    private EntityManager entityManager;
    private SnapshotEnhetYtelseBehandlingTask snapshotTask;
    private StatistikkRepository statistikkRepository;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.statistikkRepository = new StatistikkRepository(entityManager);
        this.snapshotTask = new SnapshotEnhetYtelseBehandlingTask(statistikkRepository);
        this.entityManager = entityManager;
    }

    private void leggInnEttSettMedOppgaver() {
        entityManager.persist(førstegangOppgave);
        entityManager.persist(førstegangOppgave2);
        entityManager.persist(klageOppgave);
        entityManager.persist(innsynOppgave);
        entityManager.persist(annenAvdeling);

        beslutterOppgave.leggTilOppgaveEgenskap(
            OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
        entityManager.persist(beslutterOppgave);

        beslutterOppgave2.leggTilOppgaveEgenskap(
            OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
        entityManager.persist(beslutterOppgave2);

        entityManager.persist(lukketOppgave);

        entityManager.flush();
    }

    private void avsluttOppgave(Oppgave oppgave) {
        oppgave.avsluttOppgave();
        entityManager.merge(oppgave);
        entityManager.flush();
    }

    @Test
    void taSnapshotHentResultat() {
        leggInnEttSettMedOppgaver();
        snapshotTask.doTask(ProsessTaskData.forProsessTask(SnapshotEnhetYtelseBehandlingTask.class));
        var resultater = statistikkRepository.hentInnslagEtterTidsstempel(System.currentTimeMillis() - Duration.ofDays(7).toMillis());
        assertThat(resultater).hasSize(4);
        assertThat(resultater.stream().filter(r -> AVDELING_DRAMMEN_ENHET.equals(r.getBehandlendeEnhet())).count()).isEqualTo(3);
        assertThat(resultater.stream().map(StatistikkEnhetYtelseBehandling::getAntallAvsluttet).reduce(0, Integer::sum)).isZero();

        avsluttOppgave(beslutterOppgave);
        snapshotTask.doTask(ProsessTaskData.forProsessTask(SnapshotEnhetYtelseBehandlingTask.class));
        resultater = statistikkRepository.hentInnslagEtterTidsstempel(System.currentTimeMillis() - Duration.ofDays(7).toMillis());
        assertThat(resultater.stream().map(StatistikkEnhetYtelseBehandling::getAntallAvsluttet).reduce(0, Integer::sum)).isEqualTo(1);
    }


}
