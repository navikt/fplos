package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AdminRepositoryImpl implements AdminRepository {

    private static final Logger log = LoggerFactory.getLogger(AdminRepositoryImpl.class);

    private EntityManager entityManager;

    @Inject
    public AdminRepositoryImpl(@VLPersistenceUnit EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    AdminRepositoryImpl(){
    }

    EntityManager getEntityManager() {
        return entityManager;
    }

    public void deaktiverSisteOppgave(UUID uuid) {
        getEntityManager().createNativeQuery("UPDATE OPPGAVE o SET o.AKTIV = 'N' WHERE o.EKSTERN_ID = :uuid")
                .setParameter("uuid", uuid)
                .executeUpdate();
        getEntityManager().flush();
    }

    public Oppgave hentSisteOppgave(UUID uuid) {
        Oppgave oppgave = null;
        try {
            oppgave = getEntityManager().createQuery("Select o FROM Oppgave o where o.eksternId = :uuid ORDER BY o.opprettetTidspunkt desc", Oppgave.class)
                    .setParameter("uuid", uuid)
                    .setMaxResults(1).getSingleResult();
            getEntityManager().refresh(oppgave);
        } catch (NoResultException nre) {
            log.info("Fant ingen oppgave tilknyttet behandling med uuid {}", uuid, nre);
        }
        return oppgave;
    }

    public List<OppgaveEventLogg> hentEventer(UUID uuid) {
        return getEntityManager().createQuery( "Select o FROM oppgaveEventLogg o " +
                "where o.eksternId = :uuid ORDER BY o.opprettetTidspunkt desc", OppgaveEventLogg.class)
                .setParameter("uuid", uuid).getResultList();
    }

    @Override
    public List<Oppgave> hentAlleAktiveOppgaver() {
        return getEntityManager().createQuery("Select o FROM Oppgave o where o.aktiv = true ORDER BY o.opprettetTidspunkt desc", Oppgave.class).getResultList();
    }

    @Override
    public List<EventmottakFeillogg> hentAlleMeldingerFraFeillogg() {
        return getEntityManager().createQuery("Select ef FROM eventmottakFeillogg ef where ef.Status = :status", EventmottakFeillogg.class).setParameter("status", EventmottakFeillogg.Status.FEILET).getResultList();
    }

    @Override
    public void markerFerdig(Long feilloggId) {
        getEntityManager().persist(getEntityManager()
                        .find(EventmottakFeillogg.class, feilloggId)
                        .markerFerdig());
        getEntityManager().flush();
    }

    public List<Oppgave> hentAlleOppgaverForBehandling(UUID uuid) {
        return getEntityManager().createQuery("Select o FROM Oppgave o where o.eksternId = :uuid ORDER BY o.opprettetTidspunkt desc", Oppgave.class)
                .setParameter("uuid", uuid)
                .getResultList();
    }

    @Override
    public Oppgave deaktiverOppgave(Long oppgaveId) {
        Oppgave oppgave = hentOppgave(oppgaveId);
        Reservasjon reservasjon = oppgave.getReservasjon();
        if (reservasjon != null && reservasjon.erAktiv()) {
            reservasjon.frigiReservasjon("Oppgave er avsluttet fra admin REST-tjeneste");
            lagreReservasjon(reservasjon);
        }
        oppgave.avsluttOppgave();
        internLagre(oppgave);
        entityManager.refresh(oppgave);
        return hentOppgave(oppgaveId);
    }

    @Override
    public Oppgave aktiverOppgave(Long oppgaveId) {
        Oppgave oppgave = hentOppgave(oppgaveId);
        oppgave.gjenåpneOppgave();
        internLagre(oppgave);
        entityManager.refresh(oppgave);
        return hentOppgave(oppgaveId);
    }

    private Oppgave hentOppgave(Long oppgaveId) {
        Oppgave oppgave = getEntityManager().createQuery("Select o FROM Oppgave o where o.id = :oppgaveId", Oppgave.class)
                .setParameter("oppgaveId", oppgaveId)
                .setMaxResults(1).getSingleResult();
        getEntityManager().refresh(oppgave);
        return oppgave;
    }

    private void lagreReservasjon(Reservasjon reservasjon){
        internLagre(reservasjon);
    }

    private void internLagre(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }
}
