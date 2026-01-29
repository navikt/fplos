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
        var hendelse = new MottattHendelse(hendelseUid);
        entityManager.persist(hendelse);
        entityManager.flush();
    }

    public void slettMånedsGamle() {
        entityManager.createQuery("DELETE FROM MottattHendelse WHERE opprettetTidspunkt < :foer")
            .setParameter("foer", LocalDateTime.now().minusWeeks(4))
            .executeUpdate();
        entityManager.flush();
    }

}
