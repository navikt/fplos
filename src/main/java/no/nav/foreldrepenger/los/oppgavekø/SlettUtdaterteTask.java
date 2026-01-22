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
    }

    private int slettEldreUtløpteReservasjoner() {
        var query = entityManager.createNativeQuery("delete from RESERVASJON where RESERVERT_TIL < :foer");
        var før = LocalDate.now().minusMonths(13).atStartOfDay();
        query.setParameter("foer", før);
        int deletedRows = query.executeUpdate();
        entityManager.flush();
        return deletedRows;
    }
}
