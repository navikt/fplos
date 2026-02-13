package no.nav.foreldrepenger.los.statistikk.kø;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.statistikk.StatistikkRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class HentStatistikkForKøTaskTest {

    @Mock
    private KøStatistikkTjeneste køStatistikkTjeneste;
    private StatistikkRepository statistikkRepository;
    private HentStatistikkForKøTask task;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.statistikkRepository = new StatistikkRepository(entityManager);
        this.task = new HentStatistikkForKøTask(køStatistikkTjeneste, statistikkRepository);
        this.entityManager = entityManager;
    }

    @Test
    void skal_fjerne_tidligere_snapshot_fra_statistikken_ved_ny_rutinemessig_henting_av_statistikk() {
        // arrange
        when(køStatistikkTjeneste.hentAntallOppgaver(any())).thenReturn(15);
        when(køStatistikkTjeneste.hentAntallTilgjengeligeOppgaverFor(any())).thenReturn(15);
        var oppgaveFilterId = 1L;
        var nå = LocalDateTime.now();
        entityManager.persist(new StatistikkOppgaveFilter(oppgaveFilterId, localDateTimeTilMillis(nå.minusHours(3)), nå.toLocalDate(), 1, 2, 3, 4, 5, InnslagType.REGELMESSIG));
        entityManager.persist(new StatistikkOppgaveFilter(oppgaveFilterId, localDateTimeTilMillis(nå.minusHours(2)), nå.toLocalDate(), 1, 2, 3, 4, 5, InnslagType.REGELMESSIG));
        entityManager.persist(new StatistikkOppgaveFilter(oppgaveFilterId, localDateTimeTilMillis(nå.minusHours(1).minusMinutes(13)), nå.toLocalDate(), 1, 2, 2, 4, 5, InnslagType.SNAPSHOT));
        entityManager.persist(new StatistikkOppgaveFilter(oppgaveFilterId, localDateTimeTilMillis(nå.minusHours(1)), nå.toLocalDate(), 1, 2, 2, 4, 5, InnslagType.REGELMESSIG));
        entityManager.flush();

        // act
        var prosessTaskData = ProsessTaskData.forProsessTask(HentStatistikkForKøTask.class);
        prosessTaskData.setProperty(HentStatistikkForKøTask.OPPGAVE_FILTER_ID, String.valueOf(oppgaveFilterId));
        task.doTask(prosessTaskData);
        entityManager.flush();

        // assert
        var statistikk = statistikkRepository.hentStatistikkOppgaveFilterFraFom(oppgaveFilterId, LocalDate.now().minusWeeks(1));
        assertThat(statistikk).hasSize(4);
        assertThat(statistikk)
            .extracting(StatistikkOppgaveFilter::getInnslagType)
            .doesNotContain(InnslagType.SNAPSHOT);
    }

    @Test
    void verifiser_siste_oppgave_filter_blir_hentet() {
        // arrange
        var oppgaveFilterId = 1L;
        var nå = LocalDateTime.now();
        entityManager.persist(new StatistikkOppgaveFilter(oppgaveFilterId, localDateTimeTilMillis(nå.minusHours(1)), nå.toLocalDate(), 22, 13, 2, 111, 12, InnslagType.REGELMESSIG));
        entityManager.persist(new StatistikkOppgaveFilter(oppgaveFilterId, localDateTimeTilMillis(nå.minusHours(2)), nå.toLocalDate(), 5, 2, 2, 123, 13, InnslagType.REGELMESSIG));
        entityManager.flush();

        // act
        var statistikkOppgaveFilterOpt = statistikkRepository.hentSisteStatistikkOppgaveFilter(oppgaveFilterId, Set.of(InnslagType.REGELMESSIG));

        // assert
        assertThat(statistikkOppgaveFilterOpt).isPresent();
        var statistikkOppgaveFilter = statistikkOppgaveFilterOpt.get();
        assertThat(statistikkOppgaveFilter.getInnslagType()).isEqualTo(InnslagType.REGELMESSIG);
        assertThat(statistikkOppgaveFilter.getAntallAktive()).isEqualTo(22);
        assertThat(statistikkOppgaveFilter.getAntallTilgjengelige()).isEqualTo(13);
        assertThat(statistikkOppgaveFilter.getAntallOpprettet()).isEqualTo(111);
        assertThat(statistikkOppgaveFilter.getAntallAvsluttet()).isEqualTo(12);
    }

    private static Long localDateTimeTilMillis(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
