package no.nav.foreldrepenger.los.hendelse.hendelseoppretter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;

@ApplicationScoped
public class HendelseRepository {

    private EntityManager entityManager;

    @Inject
    public HendelseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    HendelseRepository() {
        //CDI
    }

    public void lagre(Hendelse hendelse) {
        entityManager.persist(hendelse);
        entityManager.flush();
    }

    public Hendelse hent(Long id) {
        var query = entityManager.createQuery("select h from Hendelse h where h.id = :id", Hendelse.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    public void slett(Hendelse hendelse) {
        entityManager.remove(hendelse);
        entityManager.flush();
    }
}
