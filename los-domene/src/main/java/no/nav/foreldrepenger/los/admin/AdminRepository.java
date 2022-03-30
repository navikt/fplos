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

import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.SLETTET_AV_ADMIN;


@ApplicationScoped
public class AdminRepository {
    private static final String SELECT_FRA_OPPGAVE = "SELECT o from Oppgave o ";

    private EntityManager entityManager;

    @Inject
    public AdminRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    AdminRepository(){
        // CDI
    }

    public List<Oppgave> hentOppgaver(Saksnummer saksnummer) {
        return entityManager.createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.fagsakSaksnummer = :saksnummer", Oppgave.class)
                .setParameter("saksnummer", saksnummer.longValue())
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

    public Oppgave deaktiverOppgave(Long oppgaveId) {
        var oppgave = hentOppgave(oppgaveId);
        var reservasjon = oppgave.getReservasjon();
        if (reservasjon != null && reservasjon.erAktiv()) {
            reservasjon.frigiReservasjon(SLETTET_AV_ADMIN);
            lagreReservasjon(reservasjon);
        }
        oppgave.avsluttOppgave();
        internLagre(oppgave);
        entityManager.refresh(oppgave);
        return hentOppgave(oppgaveId);
    }

    public Oppgave aktiverOppgave(Long oppgaveId) {
        var oppgave = hentOppgave(oppgaveId);
        oppgave.gjenåpneOppgave();
        internLagre(oppgave);
        entityManager.refresh(oppgave);
        return hentOppgave(oppgaveId);
    }

    private Oppgave hentOppgave(Long oppgaveId) {
        var oppgave = entityManager.createQuery("Select o FROM Oppgave o where o.id = :oppgaveId", Oppgave.class)
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
