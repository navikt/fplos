package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

@ApplicationScoped
public class ReservasjonRepository {
    private EntityManager entityManager;

    public ReservasjonRepository() {
    }

    @Inject
    public ReservasjonRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Oppgave> hentSaksbehandlersSisteReserverteOppgaver(String uid) {
        return entityManager.createQuery("""
                        select o from Oppgave o
                        inner join Reservasjon r on r.oppgave = o
                        where upper(r.reservertAv) = upper( :uid )
                        and not exists(
                            select 1
                            from Oppgave o2
                            inner join Reservasjon r2 on r2.oppgave = o2
                            where o2.behandlingId = o.behandlingId
                            and o2.opprettetTidspunkt > o.opprettetTidspunkt
                            and upper(r2.reservertAv) = upper( :uid )
                        )
                        order by coalesce(r.endretTidspunkt, r.opprettetTidspunkt) desc
                        """, Oppgave.class) //$NON-NLS-1$
                .setParameter("uid", uid).setMaxResults(15).getResultList();
    }

    public List<Oppgave> hentSaksbehandlersReserverteAktiveOppgaver(String uid) {
        return entityManager.createQuery("""
                        select o from Oppgave o
                        where o.aktiv = true
                        and exists (
                            select 1 from Reservasjon r
                            where r.oppgave = o
                            and r.reservertTil > :nå
                            and upper(r.reservertAv) = upper( :uid )
                        )
                        """, Oppgave.class)
                .setParameter("nå", LocalDateTime.now())
                .setParameter("uid", uid)
                .getResultList();
    }

    public List<Reservasjon> hentAlleReservasjonerForAvdeling(String avdelingEnhet) {
        return entityManager.createQuery("""
                        select r from Reservasjon r
                        inner join Oppgave o on r.oppgave = o
                        where r.reservertTil > :nå and o.behandlendeEnhet = :behandlendeEnhet
                        order by r.reservertAv
                        """, Reservasjon.class)
                .setParameter("nå", LocalDateTime.now())
                .setParameter("behandlendeEnhet", avdelingEnhet)
                .getResultList();
    }

    public Optional<Reservasjon> hentAktivReservasjon(Long oppgaveId) {
        // første forsøk på å forberede en-til-mange relasjon mellom oppgave og reservasjon.
        // "not exists"-clause unødvendig da implisitt kun én reservertTil > :now ?
        return entityManager.createQuery("""
                select r
                from Reservasjon r
                where r.oppgave.id = :id
                and r.reservertTil > :now
                and not exists (
                    select 1
                    from Reservasjon r2
                    where r2.oppgave.id = r.oppgave.id
                    and r2.opprettetTidspunkt > r.opprettetTidspunkt
                )
                """, Reservasjon.class)
                .setParameter("id", oppgaveId)
                .setParameter("now", LocalDateTime.now())
                .getResultStream().findFirst();
    }

    public <U extends BaseEntitet> void lagre(U entity) {
        entityManager.persist(entity);
        entityManager.flush();
    }

    public <U extends BaseEntitet> void refresh(U entity) {
        entityManager.refresh(entity);
    }
}
