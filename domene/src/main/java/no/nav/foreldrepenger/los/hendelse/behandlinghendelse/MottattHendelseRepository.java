package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.jpa.QueryHints;

@ApplicationScoped
public class MottattHendelseRepository {

    private EntityManager entityManager;

    MottattHendelseRepository() {
        // CDI
    }

    @Inject
    public MottattHendelseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean hendelseErNy(String hendelseUid) {
        var query = entityManager.createQuery("from MottattHendelse where hendelse_uid=:hendelse_uid", MottattHendelse.class)
            .setParameter("hendelse_uid", hendelseUid)
            .setHint(QueryHints.HINT_READONLY, "true");
        return query.getResultList().isEmpty();
    }

    public void registrerMottattHendelse(String hendelseUid) {
        entityManager.createNativeQuery("INSERT INTO MOTTATT_HENDELSE (hendelse_uid) VALUES (:hendelse_uid)")
            .setParameter("hendelse_uid", hendelseUid)
            .executeUpdate();
        entityManager.flush();
    }

    public void slettMånedsGamle() {
        entityManager.createNativeQuery("DELETE FROM MOTTATT_HENDELSE WHERE opprettet_tid < :foer")
                .setParameter("foer", LocalDateTime.now().minusWeeks(4))
                .executeUpdate();
        entityManager.flush();
    }

}
