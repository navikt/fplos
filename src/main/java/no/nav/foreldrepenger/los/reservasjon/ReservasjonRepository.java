package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
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

    public List<SisteReserverteMetadata> hentSisteReserverteMetadata(String uid, boolean kunAktive) {
        /*
        * Første derived table henter de siste behandlingene reservert på bruker, og lager en aktuell_tid for sortering.
        *   (Prinsippet for aktuell_tid er omtrent når saksbehandler sist tok i behandlingen, og dette er sorteringen som benyttes i visning.)
        *   Svakhet:
        *       1-1 relasjon mellom oppgave og reservasjon. Dersom saksbehandler ikke har skapt aktivitet (i.e løst AP) som gir ferske oppgaver
        *       vil ev. nye reservasjoner overskrive. Alternativet pt er å bruke reservasjon_event_logg.
        * Neste join med en derived table henter event_type fra oppgave_event_logg (pt. nødvendig for ventestatus)
        * Siste join henter siste oppgave tilkyttet behandlingen, uavhengig av "eier" av denne.
        */
        var kunAktiveValue = kunAktive ? List.of("J") : List.of("J", "N");
        var query = entityManager.createNativeQuery("""
            select
                o.id as oppgave_id,
                oel.event_type as siste_event_type,
                sb_res.aktuell_tid
            from oppgave o
            join (
                select behandling_id, coalesce(r.endret_tid, r.opprettet_tid) as aktuell_tid,
                row_number() over (partition by o.behandling_id order by o.opprettet_tid desc) as rn
                from Oppgave o
                inner join Reservasjon r on r.oppgave_id = o.id
                where r.opprettet_tid > :fom
                and r.reservert_av = :uid
            ) sb_res on sb_res.behandling_id = o.behandling_id and sb_res.rn = 1
            join (
                select
                    behandling_id,
                    event_type,
                    row_number() over (partition by behandling_id order by opprettet_tid desc) as rn
                from OPPGAVE_EVENT_LOGG
            ) oel on oel.behandling_id = o.behandling_id and oel.rn = 1
            where o.opprettet_tid = (
                select max(o2.opprettet_tid)
                from oppgave o2
                where o2.behandling_id = o.behandling_id
            )
            and o.aktiv in ( :kunAktiveValue )
            order by sb_res.aktuell_tid desc
            """)
            .setParameter("uid", uid)
            .setParameter("fom", LocalDate.now().minusWeeks(3).atStartOfDay())
            .setParameter("kunAktiveValue", kunAktiveValue);

        @SuppressWarnings("unchecked")
        Stream<Object[]> result = query.getResultStream().limit(15);

        return result.map(row -> {
            var oppgaveId = ((Number) row[0]).longValue();
            var sisteEventType = OppgaveEventType.valueOf((String) row[1]);
            var aktuellTid = ((java.sql.Timestamp) row[2]).toLocalDateTime();
            return new SisteReserverteMetadata(oppgaveId, sisteEventType, aktuellTid);
        }).toList();
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
            """, Oppgave.class).setParameter("nå", LocalDateTime.now()).setParameter("uid", uid).getResultList();
    }

    public List<Reservasjon> hentAlleReservasjonerForAvdeling(String avdelingEnhet) {
        return entityManager.createQuery("""
            select r from Reservasjon r
            inner join Oppgave o on r.oppgave = o
            where r.reservertTil > :nå and o.behandlendeEnhet = :behandlendeEnhet
            order by r.reservertAv
            """, Reservasjon.class).setParameter("nå", LocalDateTime.now()).setParameter("behandlendeEnhet", avdelingEnhet).getResultList();
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
            """, Reservasjon.class).setParameter("id", oppgaveId).setParameter("now", LocalDateTime.now()).getResultStream().findFirst();
    }

    public <U extends BaseEntitet> void lagre(U entity) {
        entityManager.persist(entity);
        entityManager.flush();
    }

    public <U extends BaseEntitet> void refresh(U entity) {
        entityManager.refresh(entity);
    }
}
