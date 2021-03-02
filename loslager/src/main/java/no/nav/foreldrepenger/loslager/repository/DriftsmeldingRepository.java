package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.loslager.admin.Driftsmelding;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DriftsmeldingRepository {

    private EntityManager entityManager;

    @Inject
    public DriftsmeldingRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public DriftsmeldingRepository() {
    }

    public void lagre(Driftsmelding driftsmelding){
        entityManager.persist(driftsmelding);
        entityManager.flush();
    }


    public List<Driftsmelding> hentMeldinger() {
        var nå = LocalDateTime.now();
        return entityManager.createQuery("Select m FROM Driftsmelding m where m.aktivTil > :nå", Driftsmelding.class)
                .setParameter("nå", nå)
                .getResultList();
    }

    public void deaktiverDriftsmeldinger() {
        hentMeldinger().stream()
                .map(Driftsmelding::deaktiver)
                .forEach(this::lagre);
    }
}
