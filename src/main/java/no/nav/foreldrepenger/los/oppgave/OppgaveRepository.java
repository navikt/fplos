package no.nav.foreldrepenger.los.oppgave;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.jpa.HibernateHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringOppdaterer;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

@ApplicationScoped
public class OppgaveRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRepository.class);

    static final String COUNT_FRA_OPPGAVE = "SELECT count(1) from Oppgave o ";
    private static final String SELECT_FRA_OPPGAVE = "SELECT o from Oppgave o ";
    static final String COUNT_FRA_TILBAKEKREVING_OPPGAVE = "SELECT count(1) from TilbakekrevingOppgave o ";
    private static final String SELECT_FRA_TILBAKEKREVING_OPPGAVE = "SELECT o from TilbakekrevingOppgave o ";

    public static final String BEHANDLING_ID_FELT_SQL = "behandlingId";

    private EntityManager entityManager;

    @Inject
    public OppgaveRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OppgaveRepository() {
    }

    public int hentAntallOppgaver(Oppgavespørring oppgavespørring) {
        var selection = COUNT_FRA_OPPGAVE;
        if (KøSortering.FK_TILBAKEKREVING.equalsIgnoreCase(oppgavespørring.getSortering().getFeltkategori())) {
            selection = COUNT_FRA_TILBAKEKREVING_OPPGAVE;
        }
        var oppgaveTypedQuery = lagOppgavespørring(selection, Long.class, oppgavespørring);
        return oppgaveTypedQuery.getSingleResult().intValue();
    }

    public int hentAntallOppgaverForAvdeling(String enhetsNummer) {
        var oppgavespørring = new Oppgavespørring(enhetsNummer, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), false, null, null, null, null);
        var oppgaveTypedQuery = lagOppgavespørring(COUNT_FRA_OPPGAVE, Long.class, oppgavespørring);
        return oppgaveTypedQuery.getSingleResult().intValue();
    }

    public List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring) {
        var selection = SELECT_FRA_OPPGAVE;
        if (KøSortering.FK_TILBAKEKREVING.equalsIgnoreCase(oppgavespørring.getSortering().getFeltkategori())) {
            selection = SELECT_FRA_TILBAKEKREVING_OPPGAVE;
        }
        var query = lagOppgavespørring(selection, Oppgave.class, oppgavespørring);
        oppgavespørring.getMaxAntallOppgaver().ifPresent(max -> query.setMaxResults(max.intValue()));
        return query.getResultList();
    }

    private <T> TypedQuery<T> lagOppgavespørring(String selection, Class<T> resultClass, Oppgavespørring queryDto) {
        return OppgaveQueryMapper.lagOppgavespørring(entityManager, selection, resultClass, queryDto);
    }

    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Saksnummer> saksnummerListe) {
        return entityManager.createQuery(
            SELECT_FRA_OPPGAVE + "WHERE o.saksnummer in :saksnummerListe " + "AND o.aktiv = true " + "ORDER BY o.saksnummer desc ",
            Oppgave.class).setParameter("saksnummerListe", saksnummerListe).getResultList();
    }

    public Optional<Reservasjon> hentReservasjon(Long oppgaveId) {
        return entityManager.createQuery("from Reservasjon r WHERE r.oppgave.id = :id ", Reservasjon.class)
            .setParameter("id", oppgaveId)
            .getResultStream()
            .findFirst();
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

    public <U extends BaseEntitet> void lagre(U entitet) {
        entityManager.persist(entitet);
        entityManager.flush();
    }

    public Long lagreFiltrering(OppgaveFiltrering oppgaveFiltrering) {
        lagre(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    public void lagre(OppgaveEgenskap oppgaveEgenskap) {
        entityManager.persist(oppgaveEgenskap);
        entityManager.flush();
        refresh(oppgaveEgenskap.getOppgave());
    }

    public <U extends BaseEntitet> void slett(U entitet) {
        entityManager.remove(entitet);
        entityManager.flush();
    }

    public void oppdaterNavn(Long sakslisteId, String navn) {
        entityManager.persist(entityManager.find(OppgaveFiltreringOppdaterer.class, sakslisteId).endreNavn(navn));
        entityManager.flush();
    }

    public void slettListe(Long listeId) {
        var filtersett = entityManager.find(OppgaveFiltrering.class, listeId);
        if (filtersett != null) {
            filtersett.tilbakestill();
            entityManager.merge(filtersett);
            entityManager.flush();
            entityManager.remove(filtersett);
        }
    }

    public <U extends BaseEntitet> void refresh(U entitet) {
        entityManager.refresh(entitet);
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

    public TilbakekrevingOppgave opprettTilbakekrevingOppgave(TilbakekrevingOppgave oppgave) {
        lagre(oppgave);
        entityManager.refresh(oppgave);
        return oppgave;
    }

    public List<OppgaveEventLogg> hentOppgaveEventer(BehandlingId behandlingId) {
        Objects.requireNonNull(behandlingId, "behandlingId kan ikke være null");
        return entityManager.createQuery("""
            from oppgaveEventLogg oel
            where oel.behandlingId = :behandlingId
            order by oel.opprettetTidspunkt desc
            """, OppgaveEventLogg.class).setParameter(BEHANDLING_ID_FELT_SQL, behandlingId).getResultList();
    }

    public List<OppgaveEgenskap> hentOppgaveEgenskaper(Long oppgaveId) {
        return entityManager.createQuery("""
            from OppgaveEgenskap oe
            where oe.oppgaveId = :oppgaveId
            ORDER BY oe.id desc
            """, OppgaveEgenskap.class).setParameter("oppgaveId", oppgaveId).getResultList();
    }

    protected <T> List<T> hentOppgaver(BehandlingId behandlingId, Class<T> cls) {
        var select = cls.equals(TilbakekrevingOppgave.class) ? SELECT_FRA_TILBAKEKREVING_OPPGAVE : SELECT_FRA_OPPGAVE;
        return entityManager.createQuery(select + "WHERE o.behandlingId = :behandlingId", cls)
            .setParameter(BEHANDLING_ID_FELT_SQL, behandlingId)
            .getResultList();
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

}
