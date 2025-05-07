package no.nav.foreldrepenger.los.oppgave.tilbudtoppgave;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class TilbudtOppgaveObserver {
    private EntityManager entityManager;

    public TilbudtOppgaveObserver() {
    }

    @Inject
    public TilbudtOppgaveObserver(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // fpsak med venner bruker som regel default TransactionPhase.IN_PROGRESS, vurder hva som blir korrekt her
    public void handle(@Observes(during = TransactionPhase.AFTER_SUCCESS) TilbudtOppgave event) {
        entityManager.createQuery("update Oppgave set tilbudtCount = coalesce(tilbudtCount, 0) + 1 where id in (:ids)")
            .setParameter("ids", event.oppgaveIdListe())
            .executeUpdate();
    }
}
