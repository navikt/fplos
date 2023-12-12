package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.time.LocalDateTime;

import org.hibernate.jpa.HibernateHints;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

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
        var query = entityManager.createQuery("from MottattHendelse where hendelseUid=:hendelse_uid", MottattHendelse.class)
            .setParameter("hendelse_uid", hendelseUid)
            .setHint(HibernateHints.HINT_READ_ONLY, "true");
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
