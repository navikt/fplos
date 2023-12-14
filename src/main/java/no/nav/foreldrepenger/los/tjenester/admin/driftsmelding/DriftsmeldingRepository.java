package no.nav.foreldrepenger.los.tjenester.admin.driftsmelding;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class DriftsmeldingRepository {

    private EntityManager entityManager;

    @Inject
    public DriftsmeldingRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public DriftsmeldingRepository() {
    }

    public void lagre(Driftsmelding driftsmelding) {
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
        hentMeldinger().stream().map(Driftsmelding::deaktiver).forEach(this::lagre);
    }
}
