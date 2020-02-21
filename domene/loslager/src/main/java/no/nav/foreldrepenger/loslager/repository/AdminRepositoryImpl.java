package no.nav.foreldrepenger.loslager.repository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;

@ApplicationScoped
public class AdminRepositoryImpl implements AdminRepository {
    private static final String SELECT_FRA_OPPGAVE = "SELECT o from Oppgave o ";
    private static final Logger log = LoggerFactory.getLogger(AdminRepositoryImpl.class);

    private EntityManager entityManager;

    @Inject
    public AdminRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    AdminRepositoryImpl(){
    }

    public void deaktiverSisteOppgave(UUID eksternId) {
        List<Oppgave> oppgaver = hentOppgaverForEksternId(eksternId);
        if (oppgaver.isEmpty()) {
            return;
        }
        Oppgave nyesteOppgave = oppgaver.stream()
                .max(Comparator.comparing(Oppgave::getOpprettetTidspunkt))
                .orElse(null);
        nyesteOppgave.deaktiverOppgave();
        internLagre(nyesteOppgave);
        entityManager.refresh(nyesteOppgave);
    }
    private List<Oppgave> hentOppgaverForEksternId(UUID eksternId) {
        return entityManager.createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.eksternId = :eksternId ", Oppgave.class)
                .setParameter("eksternId", eksternId)
                .getResultList();
    }
    public Oppgave hentSisteOppgave(UUID uuid) {
        Oppgave oppgave = null;
        try {
            oppgave = entityManager.createQuery("Select o FROM Oppgave o where o.eksternId = :uuid ORDER BY o.opprettetTidspunkt desc", Oppgave.class)
                    .setParameter("uuid", uuid)
                    .setMaxResults(1).getSingleResult();
            entityManager.refresh(oppgave);
        } catch (NoResultException nre) {
            log.info("Fant ingen oppgave tilknyttet behandling med uuid {}", uuid, nre);
        }
        return oppgave;
    }

    @Override
    public TilbakekrevingOppgave hentSisteTilbakekrevingOppgave(UUID uuid) {
        TilbakekrevingOppgave oppgave = null;
        try {
            oppgave = entityManager.createQuery("Select to FROM TilbakekrevingOppgave to where to.eksternId = :eksternId ORDER BY to.opprettetTidspunkt desc", TilbakekrevingOppgave.class)
                    .setParameter("eksternId", uuid)
                    .setMaxResults(1).getSingleResult();
            entityManager.refresh(oppgave);
        } catch (NoResultException nre) {
            log.info("Fant ingen oppgave tilknyttet behandling med id {}", uuid, nre);
        }
        return oppgave;
    }

    public List<OppgaveEventLogg> hentEventer(UUID uuid) {
        return entityManager.createQuery( "Select o FROM oppgaveEventLogg o " +
                "where o.eksternId = :uuid ORDER BY o.opprettetTidspunkt desc", OppgaveEventLogg.class)
                .setParameter("uuid", uuid).getResultList();
    }

    @Override
    public List<Oppgave> hentAlleAktiveOppgaver() {
        return entityManager.createQuery("Select o FROM Oppgave o where o.aktiv = true ORDER BY o.opprettetTidspunkt desc", Oppgave.class).getResultList();
    }

    @Override
    public List<EventmottakFeillogg> hentAlleMeldingerFraFeillogg() {
        return entityManager.createQuery("Select ef FROM eventmottakFeillogg ef where ef.status = :status", EventmottakFeillogg.class).setParameter("status", EventmottakFeillogg.Status.FEILET).getResultList();
    }

    @Override
    public void markerFerdig(Long feilloggId) {
        entityManager.persist(entityManager
                        .find(EventmottakFeillogg.class, feilloggId)
                        .markerFerdig());
        entityManager.flush();
    }

    public List<Oppgave> hentAlleOppgaverForBehandling(UUID uuid) {
        return entityManager.createQuery("Select o FROM Oppgave o where o.eksternId = :uuid ORDER BY o.opprettetTidspunkt desc", Oppgave.class)
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
        Oppgave oppgave = entityManager.createQuery("Select o FROM Oppgave o where o.id = :oppgaveId", Oppgave.class)
                .setParameter("oppgaveId", oppgaveId)
                .setMaxResults(1).getSingleResult();
        entityManager.refresh(oppgave);
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
