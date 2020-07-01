package no.nav.fplos.kafkatjenester.prosesstask;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Objects;

@ApplicationScoped
public class BatchProsessTaskRepository {

    private EntityManager entityManager;
    private ProsessTaskRepository prosessTaskRepository;

    BatchProsessTaskRepository() {
        // for CDI proxying
    }

    @Inject
    public BatchProsessTaskRepository(EntityManager entityManager,
                                      ProsessTaskRepository prosessTaskRepository) {
        Objects.requireNonNull(entityManager, "entityManager");
        this.entityManager = entityManager;
        this.prosessTaskRepository = prosessTaskRepository;
    }


    int rekjørAlleFeiledeTasks() {
        Query query = entityManager.createNativeQuery("UPDATE PROSESS_TASK " +
                "SET status = :status, " +
                "feilede_forsoek = feilede_forsoek-1, " +
                "neste_kjoering_etter = current_timestamp " +
                "WHERE STATUS = :feilet");
        query.setParameter("status", ProsessTaskStatus.KLAR.getDbKode())
                .setParameter("feilet", ProsessTaskStatus.FEILET.getDbKode());
        int updatedRows = query.executeUpdate();
        entityManager.flush();

        return updatedRows;
    }

}
