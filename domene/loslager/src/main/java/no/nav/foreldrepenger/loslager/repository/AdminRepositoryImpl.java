package no.nav.foreldrepenger.loslager.repository;

import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;

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
        // CDI
    }

    public void deaktiverSisteOppgave(BehandlingId behandlingId) {
        List<Oppgave> oppgaver = hentOppgaverForBehandling(behandlingId);
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
    private List<Oppgave> hentOppgaverForBehandling(BehandlingId behandlingId) {
        return entityManager.createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.behandlingId = :behandlingId ", Oppgave.class)
                .setParameter("behandlingId", behandlingId)
                .getResultList();
    }
    public Oppgave hentSisteOppgave(BehandlingId behandlingId) {
        Oppgave oppgave = null;
        try {
            oppgave = entityManager.createQuery("Select o FROM Oppgave o where o.behandlingId = :behandlingId ORDER BY o.opprettetTidspunkt desc", Oppgave.class)
                    .setParameter("behandlingId", behandlingId)
                    .setMaxResults(1).getSingleResult();
            entityManager.refresh(oppgave);
        } catch (NoResultException nre) {
            log.info("Fant ingen oppgave tilknyttet behandling med id {}", behandlingId, nre);
        }
        return oppgave;
    }

    public List<OppgaveEventLogg> hentEventer(BehandlingId behandlingId) {
        return entityManager.createQuery( "Select o FROM oppgaveEventLogg o " +
                "where o.behandlingId = :behandlingId ORDER BY o.opprettetTidspunkt desc", OppgaveEventLogg.class)
                .setParameter("behandlingId", behandlingId).getResultList();
    }

    public List<Oppgave> hentAlleOppgaverForBehandling(BehandlingId behandlingId) {
        return entityManager.createQuery("Select o FROM Oppgave o where o.behandlingId = :behandlingId ORDER BY o.opprettetTidspunkt desc", Oppgave.class)
                .setParameter("behandlingId", behandlingId)
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
