package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.loslager.oppgave.EksternIdentifikator;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Optional;

@ApplicationScoped
public class EksternIdentifikatorRepositoryImpl implements EksternIdentifikatorRepository {

    private EntityManager entityManager;
    @Inject
    public EksternIdentifikatorRepositoryImpl(@VLPersistenceUnit EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EksternIdentifikator finnEllerOpprettEksternId(String fagsystem, String eksternRefId) {
        Optional<EksternIdentifikator> eksternIdOptional = finnIdentifikator(fagsystem, eksternRefId);
        return eksternIdOptional.orElseGet(() -> lagre(EksternIdentifikator.builder().medSystem(fagsystem).medEksternRefId(eksternRefId).build()));
    }

    @Override
    public Optional<EksternIdentifikator> finnIdentifikator(String fagsystem, String eksternRefId) {
        return entityManager.createQuery("SELECT ekstId FROM EksternIdentifikator ekstId " +
                "where ekstId.system = :system AND ekstId.eksternRefId = :eksternRefId", EksternIdentifikator.class)
                .setParameter("system", fagsystem).setParameter("eksternRefId", eksternRefId).getResultStream().findFirst();
    }

    @Override
    public EksternIdentifikator lagre(EksternIdentifikator eksternIdentifikator) {
        entityManager.persist(eksternIdentifikator);
        entityManager.flush();
        entityManager.refresh(eksternIdentifikator);
        return eksternIdentifikator;
    }
}
