package no.nav.foreldrepenger.los.oppgavekø;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Dependent
@ProsessTask(value = "vedlikehold.slettutdaterte", cronExpression = "30 15 2 * * *", maxFailedRuns = 1)
public class SlettUtdaterteTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SlettUtdaterteTask.class);
    private final EntityManager entityManager;

    @Inject
    public SlettUtdaterteTask(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        int slettetReservasjonerAntall = slettEldreUtløpteReservasjoner();
        LOG.info("Slettet {} utdaterte reservasjoner", slettetReservasjonerAntall);
        int slettetOelAntall = slettOel();
        LOG.info("Slettet {} utdaterte oppgave_event_logg uten aktiv oppgave", slettetOelAntall);
    }

    private int slettEldreUtløpteReservasjoner() {
        var query = entityManager.createNativeQuery("delete from RESERVASJON where RESERVERT_TIL < :foer");
        var før = LocalDate.now().minusMonths(13).atStartOfDay();
        query.setParameter("foer", før);
        int deletedRows = query.executeUpdate();
        entityManager.flush();
        return deletedRows;
    }

    private int slettOel() {
        var før = LocalDate.now().minusMonths(13).atStartOfDay();
        var kjøretidDeadline = LocalDateTime.now().plusMinutes(2);
        int batchSize = 10_000;
        int totalDeleted = 0;

        while (true) {
            var query = entityManager.createNativeQuery("""
                delete from OPPGAVE_EVENT_LOGG oel
                where ROWID in (
                    select ROWID from OPPGAVE_EVENT_LOGG oel2
                    where oel2.OPPRETTET_TID < :foer
                    and not exists (
                        select 1 from OPPGAVE o where o.BEHANDLING_ID = oel2.BEHANDLING_ID and o.AKTIV = 'J'
                    )
                    and ROWNUM <= :batchSize
                )
                """);
            query.setParameter("foer", før);
            query.setParameter("batchSize", batchSize);
            int deleted = query.executeUpdate();
            entityManager.flush();
            totalDeleted += deleted;

            if (deleted < batchSize || LocalDateTime.now().isAfter(kjøretidDeadline)) {
                break;
            }
        }

        return totalDeleted;
    }
}
