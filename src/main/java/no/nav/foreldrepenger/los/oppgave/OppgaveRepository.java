package no.nav.foreldrepenger.los.oppgave;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.jpa.HibernateHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringOppdaterer;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

@ApplicationScoped
public class OppgaveRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRepository.class);

    public static final String BEHANDLING_ID_FELT_SQL = "behandlingId";

    private EntityManager entityManager;

    @Inject
    public OppgaveRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OppgaveRepository() {
    }

    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Saksnummer> saksnummerListe) {
        return entityManager.createQuery("from Oppgave o where o.saksnummer in :saksnummerListe and o.aktiv = true order by o.saksnummer desc",
            Oppgave.class).setParameter("saksnummerListe", saksnummerListe).getResultList();
    }

    public Optional<Reservasjon> hentReservasjon(Long oppgaveId) {
        return entityManager.createQuery("from Reservasjon r WHERE r.oppgave.id = :id ", Reservasjon.class)
            .setParameter("id", oppgaveId)
            .getResultStream()
            .findFirst();
    }

    public List<OppgaveFiltrering> hentAlleOppgaveFiltreReadOnly() {
        var listeTypedQuery = entityManager.createQuery("from OppgaveFiltrering", OppgaveFiltrering.class);
        listeTypedQuery.setHint(HibernateHints.HINT_READ_ONLY, "true");
        return listeTypedQuery.getResultList();
    }

    public List<OppgaveFiltrering> hentAlleOppgaveFilterSettTilknyttetEnhet(String avdelingEnhet) {
        var listeTypedQuery = entityManager.createQuery("from OppgaveFiltrering l where l.avdeling.avdelingEnhet = :avdelingEnhet order by l.navn",
            OppgaveFiltrering.class).setParameter("avdelingEnhet", avdelingEnhet);//$NON-NLS-1$
        return listeTypedQuery.getResultList();
    }

    public Optional<OppgaveFiltrering> hentOppgaveFilterSett(Long listeId) {
        var listeTypedQuery = entityManager.createQuery("FROM OppgaveFiltrering l WHERE l.id = :id ", OppgaveFiltrering.class)
            .setParameter("id", listeId);
        return listeTypedQuery.getResultStream().findFirst();
    }

    public Long lagreFiltrering(OppgaveFiltrering oppgaveFiltrering) {
        lagre(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    public void oppdaterNavn(Long sakslisteId, String navn) {
        entityManager.persist(entityManager.find(OppgaveFiltreringOppdaterer.class, sakslisteId).endreNavn(navn));
        entityManager.flush();
    }

    public void slettListe(Long listeId) {
        var filtersett = entityManager.find(OppgaveFiltrering.class, listeId);
        entityManager.remove(filtersett);
    }

    public boolean sjekkOmOppgaverFortsattErTilgjengelige(List<Long> oppgaveIder) {
        var fortsattTilgjengelige = entityManager.createQuery("""
            select count(o.id) from Oppgave o
            where not exists (
                select 1
                from Reservasjon r
                where r.oppgave = o
                and r.reservertTil > :nå
            )
            and o.id in ( :oppgaveId )
            and o.aktiv = true
            """, Long.class)
            .setParameter("nå", LocalDateTime.now())
            .setParameter("oppgaveId", oppgaveIder)
            .getSingleResult();
        return oppgaveIder.size() == fortsattTilgjengelige.intValue();
    }

    public void settSortering(Long sakslisteId, String sortering) {
        entityManager.persist(entityManager.find(OppgaveFiltreringOppdaterer.class, sakslisteId)
            .endreSortering(sortering)
            .endreErDynamiskPeriode(false)
            .endreFomDato(null)
            .endreTomDato(null)
            .endreFraVerdi(null)
            .endreTilVerdi(null));
        entityManager.flush();
    }

    public void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato) {
        entityManager.persist(entityManager.find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
            .endreErDynamiskPeriode(false)
            .endreFraVerdi(null)
            .endreTilVerdi(null)
            .endreFomDato(fomDato)
            .endreTomDato(tomDato));
        entityManager.flush();
    }

    public void settSorteringNumeriskIntervall(Long oppgaveFiltreringId, Long fra, Long til) {
        entityManager.persist(entityManager.find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
            .endreErDynamiskPeriode(true)
            .endreFomDato(null)
            .endreTomDato(null)
            .endreFraVerdi(fra)
            .endreTilVerdi(til));
        entityManager.flush();
    }

    public void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode) {
        entityManager.persist(entityManager.find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
            .endreErDynamiskPeriode(erDynamiskPeriode)
            .endreFomDato(null)
            .endreTomDato(null)
            .endreFraVerdi(null)
            .endreTilVerdi(null));
        entityManager.flush();
    }

    public Oppgave hentOppgave(Long oppgaveId) {
        return entityManager.createQuery("FROM Oppgave o where o.id = :id", Oppgave.class).setParameter("id", oppgaveId).getSingleResult();
    }

    public List<Oppgave> hentOppgaverReadOnly(List<Long> oppgaveIder) {
        if (oppgaveIder == null || oppgaveIder.isEmpty()) {
            return List.of();
        }
        return entityManager.createQuery("FROM Oppgave o where o.id IN (:oppgaveIder)", Oppgave.class)
            .setParameter("oppgaveIder", oppgaveIder)
            .setHint(HibernateHints.HINT_READ_ONLY, "true")
            .getResultList();
    }

    public List<Oppgave> hentOppgaver(BehandlingId behandlingId) {
        return entityManager.createQuery("FROM Oppgave o where o.behandlingId = :behandlingId", Oppgave.class)
            .setParameter(BEHANDLING_ID_FELT_SQL, behandlingId)
            .getResultList();
    }

    public Optional<Oppgave> hentAktivOppgave(BehandlingId behandlingId) {
        var oppgaver = entityManager.createQuery("""
            FROM Oppgave o
            where o.behandlingId = :behandlingId
            and o.aktiv = :aktiv
            """, Oppgave.class).setParameter(BEHANDLING_ID_FELT_SQL, behandlingId).setParameter("aktiv", true).getResultList();
        if (oppgaver.size() > 1) {
            LOG.warn("Flere enn én aktive oppgaver for behandlingId {}", behandlingId);
        }
        return oppgaver.stream().max(Comparator.comparing(Oppgave::getOpprettetTidspunkt));
    }

    public Oppgave opprettOppgave(Oppgave oppgave) {
        entityManager.persist(oppgave);
        return oppgave;
    }

    public <U extends BaseEntitet> void lagre(U entitet) {
        entityManager.persist(entitet);
        entityManager.flush();
    }

    public <U extends BaseEntitet> void refresh(U entitet) {
        entityManager.refresh(entitet);
    }

    public Optional<Behandling> finnBehandling(UUID behandlingId) {
        return Optional.ofNullable(entityManager.find(Behandling.class, behandlingId));
    }

    public List<Behandling> finnBehandlinger(Set<UUID> behandlingIder) {
        return entityManager.createQuery("FROM Behandling where id in (:behandlingIder)", Behandling.class)
            .setParameter("behandlingIder", behandlingIder)
            .getResultList();
    }

    public void lagreBehandling(Behandling behandling) {
        entityManager.persist(behandling);
    }

    public void lagreFlushBehandling(Behandling behandling) {
        entityManager.persist(behandling);
        entityManager.flush();
    }

    public List<BehandlingEgenskap> finnBehandlingEgenskaper(UUID behandlingId) {
        return entityManager.createQuery("FROM BehandlingEgenskap where behandlingId = :behandlingId", BehandlingEgenskap.class)
            .setParameter(BEHANDLING_ID_FELT_SQL, behandlingId)
            .getResultList();
    }

    public Set<AndreKriterierType> finnBehandlingKriterier(UUID behandlingId) {
        return entityManager.createQuery("FROM BehandlingEgenskap where behandlingId = :behandlingId", BehandlingEgenskap.class)
            .setParameter(BEHANDLING_ID_FELT_SQL, behandlingId)
            .getResultList().stream()
            .map(BehandlingEgenskap::getAndreKriterierType)
            .collect(Collectors.toSet());
    }

    public void nyeBehandlingEgenskaper(UUID behandlingId, Set<AndreKriterierType> andreKriterierType) {
        andreKriterierType.stream()
            .map(a -> new BehandlingEgenskap(behandlingId, a))
            .forEach(entityManager::persist);
        if (!andreKriterierType.isEmpty()) {
            entityManager.flush();
        }
    }

    public void fjernBehandlingEgenskaper(UUID behandlingId, Set<AndreKriterierType> andreKriterierType) {
        if (andreKriterierType.isEmpty()) {
            return;
        }
        finnBehandlingEgenskaper(behandlingId).stream()
            .filter(e -> andreKriterierType.contains(e.getAndreKriterierType()))
            .forEach(e -> entityManager.remove(e));
        if (!andreKriterierType.isEmpty()) {
            entityManager.flush();
        }
    }

}
