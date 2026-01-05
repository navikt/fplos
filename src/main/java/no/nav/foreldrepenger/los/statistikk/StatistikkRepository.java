package no.nav.foreldrepenger.los.statistikk;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.statistikk.kø.StatistikkForKø;

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

    public void lagreStatistikkForKø(StatistikkForKø statistikk) {
        entityManager.persist(statistikk);
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

    public List<StatistikkEnhetYtelseBehandling> hentStatistikkForEnhetFomDato(String enhet, LocalDate fom) {
        var startpunkt = fom.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return entityManager.createQuery("""
            SELECT s FROM StatistikkEnhetYtelseBehandling s
            WHERE s.behandlendeEnhet = :enhet AND s.tidsstempel >= :tidsstempel
            ORDER BY s.tidsstempel, s.fagsakYtelseType, s.behandlingType
            """, StatistikkEnhetYtelseBehandling.class)
            .setParameter("enhet", enhet)
            .setParameter("tidsstempel", startpunkt)
            .getResultList();
    }

    public List<StatistikkEnhetYtelseBehandling> hentStatistikkFomDato(LocalDate fom) {
        var startpunkt = fom.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return entityManager.createQuery("""
            SELECT s FROM StatistikkEnhetYtelseBehandling s
            WHERE s.tidsstempel >= :tidsstempel
            ORDER BY s.tidsstempel, s.fagsakYtelseType, s.behandlingType
            """, StatistikkEnhetYtelseBehandling.class)
            .setParameter("tidsstempel", startpunkt)
            .getResultList();
    }
}
