package no.nav.foreldrepenger.los.statistikk;

import no.nav.foreldrepenger.los.statistikk.kø.InnslagType;
import no.nav.foreldrepenger.los.statistikk.kø.StatistikkOppgaveFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class StatistikkRepositoryTest {

    private StatistikkRepository statistikkRepository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.statistikkRepository = new StatistikkRepository(entityManager);
        this.entityManager = entityManager;
    }

    @Test
    void skal_returner_en_rad_for_hver_id_hvor_tidspunktet_er_størst() {
        // Arrange
        var nå = LocalDateTime.now();
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(1L, toMs(nå.minusHours(1)), nå.toLocalDate(), 1, 0, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(1L, toMs(nå.minusHours(2)), nå.toLocalDate(), 2, 1, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(1L, toMs(nå.minusHours(3)), nå.toLocalDate(), 3, 2, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(1L, toMs(nå.minusHours(4)), nå.toLocalDate(), 4, 3, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(1L, toMs(nå.minusHours(5)), nå.toLocalDate(), 5, 4, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(2L, toMs(nå.minusHours(1)), nå.toLocalDate(), 1, 0, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(2L, toMs(nå.minusHours(2)), nå.toLocalDate(), 2, 1, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(2L, toMs(nå.minusHours(3)), nå.toLocalDate(), 3, 2, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(2L, toMs(nå.minusHours(4)), nå.toLocalDate(), 4, 3, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(2L, toMs(nå.minusHours(5)), nå.toLocalDate(), 5, 4, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(3L, toMs(nå.minusHours(1)), nå.toLocalDate(), 1, 4, InnslagType.REGELMESSIG));
        statistikkRepository.lagreStatistikkOppgaveFilter(new StatistikkOppgaveFilter(3L, toMs(nå.minusHours(5)), nå.toLocalDate(), 2, 2, InnslagType.REGELMESSIG));
        entityManager.flush();


        // Act
        var statistikkMap = statistikkRepository.hentSisteStatistikkForAlleOppgaveFiltre();

        // Assert
        assertThat(statistikkMap).hasSize(3);
        assertThat(statistikkMap.get(1L).getAntallAktive()).isEqualTo(1);
        assertThat(statistikkMap.get(2L).getAntallAktive()).isEqualTo(1);
        assertThat(statistikkMap.get(3L).getAntallAktive()).isEqualTo(1);
    }

    private static Long toMs(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
