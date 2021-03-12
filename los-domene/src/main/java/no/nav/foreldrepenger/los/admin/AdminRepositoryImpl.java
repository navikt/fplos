package no.nav.foreldrepenger.los.admin;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;


@ApplicationScoped
public class AdminRepositoryImpl implements AdminRepository {
    private static final String SELECT_FRA_OPPGAVE = "SELECT o from Oppgave o ";

    private EntityManager entityManager;

    @Inject
    public AdminRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    AdminRepositoryImpl(){
        // CDI
    }

    @Override
    public List<Oppgave> hentOppgaver(Saksnummer saksnummer) {
        return entityManager.createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.fagsakSaksnummer = :saksnummer", Oppgave.class)
                .setParameter("saksnummer", Long.valueOf(saksnummer.getVerdi()))
                .getResultList();
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
