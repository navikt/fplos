package no.nav.foreldrepenger.los.statistikk;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class StatistikkRepository {

    private EntityManager entityManager;

    @Inject
    public StatistikkRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    StatistikkRepository() {
        //CDI
    }

    public void lagreStatistikkEnhetYtelseBehandling(Collection<StatistikkEnhetYtelseBehandling> statistikk) {
        statistikk.forEach(innslag -> entityManager.persist(innslag));
        entityManager.flush();
    }

    public List<OppgaveEnhetYtelseBehandling> hentÅpneOppgaverPerEnhetYtelseBehandling() {
        return entityManager.createQuery("""
            Select new no.nav.foreldrepenger.los.statistikk.OppgaveEnhetYtelseBehandling(
                o.behandlendeEnhet, o.fagsakYtelseType, o.behandlingType, Count(o.id))
            FROM Oppgave o
            WHERE aktiv = true
            GROUP BY o.behandlendeEnhet, o.fagsakYtelseType, o.behandlingType
            """, OppgaveEnhetYtelseBehandling.class).getResultList();
    }

    public List<OppgaveEnhetYtelseBehandling> hentOpprettetOppgaverPerEnhetYtelseBehandling() {
        return entityManager.createQuery("""
            Select new no.nav.foreldrepenger.los.statistikk.OppgaveEnhetYtelseBehandling(
                o.behandlendeEnhet, o.fagsakYtelseType, o.behandlingType, Count(o.id))
            FROM Oppgave o
            WHERE o.opprettetTidspunkt > :opprettet
            GROUP BY o.behandlendeEnhet, o.fagsakYtelseType, o.behandlingType
            """, OppgaveEnhetYtelseBehandling.class)
            .setParameter("opprettet", LocalDateTime.now().minusHours(24))
            .getResultList();
    }

    public List<OppgaveEnhetYtelseBehandling> hentAvsluttetOppgaverPerEnhetYtelseBehandling() {
        return entityManager.createQuery("""
            Select new no.nav.foreldrepenger.los.statistikk.OppgaveEnhetYtelseBehandling(
                o.behandlendeEnhet, o.fagsakYtelseType, o.behandlingType, Count(o.id))
            FROM Oppgave o
            WHERE o.aktiv = false and o.oppgaveAvsluttet > :endret
            GROUP BY o.behandlendeEnhet, o.fagsakYtelseType, o.behandlingType
            """, OppgaveEnhetYtelseBehandling.class)
            .setParameter("endret", LocalDateTime.now().minusHours(24))
            .getResultList();
    }

    public List<StatistikkEnhetYtelseBehandling> hentInnslagEtterTidsstempel(Long tidsstempel) {
        return entityManager.createQuery("SELECT s FROM StatistikkEnhetYtelseBehandling s WHERE s.tidsstempel >= :tidsstempel",
                StatistikkEnhetYtelseBehandling.class)
            .setParameter("tidsstempel", tidsstempel)
            .getResultList();
    }
}
