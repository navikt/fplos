package no.nav.foreldrepenger.los.felles.prosesstask;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;

@ApplicationScoped
public class BatchProsessTaskRepository {

    private EntityManager entityManager;

    BatchProsessTaskRepository() {
        // for CDI proxying
    }

    @Inject
    public BatchProsessTaskRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager");
        this.entityManager = entityManager;
    }


    int rekjørAlleFeiledeTasks() {
        var query = entityManager.createNativeQuery("UPDATE PROSESS_TASK " +
                "SET status = :status, " +
                "feilede_forsoek = feilede_forsoek-1, " +
                "neste_kjoering_etter = current_timestamp " +
                "WHERE STATUS = :feilet");
        query.setParameter("status", ProsessTaskStatus.KLAR.getDbKode())
                .setParameter("feilet", ProsessTaskStatus.FEILET.getDbKode());
        var updatedRows = query.executeUpdate();
        entityManager.flush();

        return updatedRows;
    }

}
