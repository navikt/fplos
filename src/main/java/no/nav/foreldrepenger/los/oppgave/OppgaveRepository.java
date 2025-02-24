package no.nav.foreldrepenger.los.oppgave;


import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.FT_DATO;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.FT_HELTALL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringOppdaterer;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

@ApplicationScoped
public class OppgaveRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRepository.class);

    private static final String COUNT_FRA_OPPGAVE = "SELECT count(1) from Oppgave o ";
    private static final String SELECT_FRA_OPPGAVE = "SELECT o from Oppgave o ";
    private static final String COUNT_FRA_TILBAKEKREVING_OPPGAVE = "SELECT count(1) from TilbakekrevingOppgave o ";
    private static final String SELECT_FRA_TILBAKEKREVING_OPPGAVE = "SELECT o from TilbakekrevingOppgave o ";

    private static final String SORTERING = "ORDER BY ";
    private static final String SYNKENDE_REKKEFØLGE = " DESC";
    private static final String BEHANDLINGSFRIST = "o.behandlingsfrist";
    private static final String BEHANDLINGOPPRETTET = "o.behandlingOpprettet";
    private static final String FØRSTE_STØNADSDAG = "o.førsteStønadsdag";
    private static final String BELØP = "o.beløp";
    private static final String FEILUTBETALINGSTART = "o.feilutbetalingstart";
    private static final String OPPGAVEFILTRERING_SORTERING_NAVN = "ORDER BY l.navn";
    private static final String BEHANDLING_ID = "behandlingId";

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

    public int hentAntallOppgaverForAvdeling(Long avdelingsId) {
        var oppgavespørring = new Oppgavespørring(avdelingsId, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), false, null, null, null, null);
        var oppgaveTypedQuery = lagOppgavespørring(COUNT_FRA_OPPGAVE, Long.class, oppgavespørring);
        return oppgaveTypedQuery.getSingleResult().intValue();
    }

    public List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring) {
        return hentOppgaver(oppgavespørring, 0);
    }

    public List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring, int maksAntall) {
        var selection = SELECT_FRA_OPPGAVE;
        if (KøSortering.FK_TILBAKEKREVING.equalsIgnoreCase(oppgavespørring.getSortering().getFeltkategori())) {
            selection = SELECT_FRA_TILBAKEKREVING_OPPGAVE;
        }
        var oppgaveTypedQuery = lagOppgavespørring(selection, Oppgave.class, oppgavespørring);
        if (maksAntall > 0) {
            oppgaveTypedQuery.setMaxResults(maksAntall);
        }
        return oppgaveTypedQuery.getResultList();
    }

    private static String andreKriterierSubquery(Oppgavespørring queryDto) {
        final UnaryOperator<String> template = kode -> String.format(
            "( SELECT 1 FROM OppgaveEgenskap oe WHERE o = oe.oppgave AND oe.aktiv = true AND oe.andreKriterierType = '%s') ", kode);
        var inkluderKriterier = queryDto.getInkluderAndreKriterierTyper()
            .stream()
            .map(AndreKriterierType::getKode)
            .map(k -> "AND EXISTS " + template.apply(k));
        var ekskluderKriterier = queryDto.getEkskluderAndreKriterierTyper()
            .stream()
            .map(AndreKriterierType::getKode)
            .map(k -> "AND NOT EXISTS " + template.apply(k));
        return Stream.concat(inkluderKriterier, ekskluderKriterier).collect(Collectors.joining("\n"));
    }

    private static String filtrerBehandlingType(Oppgavespørring queryDto) {
        return queryDto.getBehandlingTyper().isEmpty() ? "" : "AND o.behandlingType in :behtyper ";
    }

    private static String filtrerYtelseType(Oppgavespørring queryDto) {
        return queryDto.getYtelseTyper().isEmpty() ? "" : "AND o.fagsakYtelseType in :fagsakYtelseType ";
    }

    private <T> TypedQuery<T> lagOppgavespørring(String selection, Class<T> oppgaveClass, Oppgavespørring queryDto) {
        var query = entityManager.createQuery(selection + //$NON-NLS-1$ // NOSONAR
            "INNER JOIN avdeling a ON a.avdelingEnhet = o.behandlendeEnhet WHERE 1=1 " + filtrerBehandlingType(queryDto) + filtrerYtelseType(queryDto)
            + andreKriterierSubquery(queryDto) + reserverteSubquery(queryDto) + tilBeslutter(queryDto) + avgrenseTilOppgaveId(queryDto)
            + "AND a.id = :enhet " + "AND o.aktiv = true " + sortering(queryDto), oppgaveClass);

        query.setParameter("enhet", queryDto.getEnhetId());
        if (!queryDto.ignorerReserversjoner()) {
            query.setParameter("nå", LocalDateTime.now());
        }
        if (!queryDto.getForAvdelingsleder()) {
            query.setParameter("tilbeslutter", AndreKriterierType.TIL_BESLUTTER).setParameter("uid", BrukerIdent.brukerIdent());
        }
        if (!queryDto.getBehandlingTyper().isEmpty()) {
            query.setParameter("behtyper", queryDto.getBehandlingTyper());
        }
        if (!queryDto.getYtelseTyper().isEmpty()) {
            query.setParameter("fagsakYtelseType", queryDto.getYtelseTyper());
        }

        if (FT_HELTALL.equalsIgnoreCase(queryDto.getSortering().getFelttype())) {
            if (queryDto.getFiltrerFra() != null) {
                query.setParameter("filterFra", BigDecimal.valueOf(queryDto.getFiltrerFra()));
            }
            if (queryDto.getFiltrerTil() != null) {
                query.setParameter("filterTil", BigDecimal.valueOf(queryDto.getFiltrerTil()));
            }
        } else if (FT_DATO.equalsIgnoreCase(queryDto.getSortering().getFelttype())) {
            if (queryDto.getFiltrerFra() != null) {
                query.setParameter("filterFomDager", KøSortering.FØRSTE_STØNADSDAG.equals(queryDto.getSortering()) ? LocalDate.now()
                    .plusDays(queryDto.getFiltrerFra()) : LocalDateTime.now().plusDays(queryDto.getFiltrerFra()).with(LocalTime.MIN));
            }
            if (queryDto.getFiltrerTil() != null) {
                query.setParameter("filterTomDager", KøSortering.FØRSTE_STØNADSDAG.equals(queryDto.getSortering()) ? LocalDate.now()
                    .plusDays(queryDto.getFiltrerTil()) : LocalDateTime.now().plusDays(queryDto.getFiltrerTil()).with(LocalTime.MAX));
            }
            if (queryDto.getFiltrerFomDato() != null) {
                query.setParameter("filterFomDato",
                    KøSortering.FØRSTE_STØNADSDAG.equals(queryDto.getSortering()) ? queryDto.getFiltrerFomDato() : queryDto.getFiltrerFomDato()
                        .atTime(LocalTime.MIN));
            }
            if (queryDto.getFiltrerTomDato() != null) {
                query.setParameter("filterTomDato",
                    KøSortering.FØRSTE_STØNADSDAG.equals(queryDto.getSortering()) ? queryDto.getFiltrerTomDato() : queryDto.getFiltrerTomDato()
                        .atTime(LocalTime.MAX));
            }
        }

        return query;
    }

    private static String reserverteSubquery(Oppgavespørring queryDto) {
        return queryDto.ignorerReserversjoner() ? "" : "AND NOT EXISTS (select r from Reservasjon r where r.oppgave = o and r.reservertTil > :nå) ";
    }

    private static String avgrenseTilOppgaveId(Oppgavespørring queryDto) {
        return queryDto.getAvgrenseTilOppgaveId().map(oppgaveId -> String.format("AND o.id = %s ", oppgaveId)).orElse("");
    }

    private static String tilBeslutter(Oppgavespørring dto) {
        return dto.getForAvdelingsleder() ? "" : """
            AND NOT EXISTS (
                select oetilbesl.oppgave from OppgaveEgenskap oetilbesl
                where oetilbesl.oppgave = o
                    AND oetilbesl.aktiv = true
                    AND oetilbesl.andreKriterierType = :tilbeslutter
                    AND upper(oetilbesl.sisteSaksbehandlerForTotrinn) = upper(:uid)
            )""";
    }

    private String sortering(Oppgavespørring oppgavespørring) {
        var sortering = oppgavespørring.getSortering();
        if (KøSortering.BEHANDLINGSFRIST.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(BEHANDLINGSFRIST, oppgavespørring.getFiltrerFra(),
                oppgavespørring.getFiltrerTil()) : filtrerStatisk(BEHANDLINGSFRIST, oppgavespørring.getFiltrerFomDato(),
                oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.OPPRETT_BEHANDLING.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(BEHANDLINGOPPRETTET, oppgavespørring.getFiltrerFra(),
                oppgavespørring.getFiltrerTil()) : filtrerStatisk(BEHANDLINGOPPRETTET, oppgavespørring.getFiltrerFomDato(),
                oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.FØRSTE_STØNADSDAG.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(FØRSTE_STØNADSDAG, oppgavespørring.getFiltrerFra(),
                oppgavespørring.getFiltrerTil()) : filtrerStatisk(FØRSTE_STØNADSDAG, oppgavespørring.getFiltrerFomDato(),
                oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.BELØP.equals(sortering)) {
            return filtrerNumerisk(BELØP, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil());
        }
        if (KøSortering.FEILUTBETALINGSTART.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(FEILUTBETALINGSTART, oppgavespørring.getFiltrerFra(),
                oppgavespørring.getFiltrerTil()) : filtrerStatisk(FEILUTBETALINGSTART, oppgavespørring.getFiltrerFomDato(),
                oppgavespørring.getFiltrerTomDato());
        }
        return SORTERING + BEHANDLINGOPPRETTET;
    }

    private String filtrerNumerisk(String sortering, Long fra, Long til) {
        var numeriskFiltrering = "";
        if (fra != null && til != null) {
            numeriskFiltrering = "AND " + sortering + " >= :filterFra AND " + sortering + " <= :filterTil ";
        } else if (fra != null) {
            numeriskFiltrering = "AND " + sortering + " >= :filterFra ";
        } else if (til != null) {
            numeriskFiltrering = "AND " + sortering + " <= :filterTil ";
        }
        return numeriskFiltrering + SORTERING + sortering + SYNKENDE_REKKEFØLGE;
    }

    private String filtrerDynamisk(String sortering, Long fomDager, Long tomDager) {
        var datoFiltrering = "";
        if (fomDager != null && tomDager != null) {
            datoFiltrering = "AND " + sortering + " > :filterFomDager AND " + sortering + " < :filterTomDager ";
        } else if (fomDager != null) {
            datoFiltrering = "AND " + sortering + " > :filterFomDager ";
        } else if (tomDager != null) {
            datoFiltrering = "AND " + sortering + " < :filterTomDager ";
        }
        return datoFiltrering + SORTERING + sortering;
    }

    private String filtrerStatisk(String sortering, LocalDate fomDato, LocalDate tomDato) {
        var datoFiltrering = "";
        if (fomDato != null && tomDato != null) {
            datoFiltrering = "AND " + sortering + " >= :filterFomDato AND " + sortering + " <= :filterTomDato ";
        } else if (fomDato != null) {
            datoFiltrering = "AND " + sortering + " >= :filterFomDato ";
        } else if (tomDato != null) {
            datoFiltrering = "AND " + sortering + " <= :filterTomDato ";
        }
        return datoFiltrering + SORTERING + sortering;
    }

    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe) {
        return entityManager.createQuery(
            SELECT_FRA_OPPGAVE + "WHERE o.fagsakSaksnummer in :fagsakSaksnummerListe " + "AND o.aktiv = true " + "ORDER BY o.fagsakSaksnummer desc ",
            Oppgave.class).setParameter("fagsakSaksnummerListe", fagsakSaksnummerListe).getResultList();
    }

    public Optional<Reservasjon> hentReservasjon(Long oppgaveId) {
        return entityManager.createQuery("from Reservasjon r WHERE r.oppgave.id = :id ", Reservasjon.class)
            .setParameter("id", oppgaveId)
            .getResultStream()
            .findFirst();
    }

    public List<OppgaveFiltrering> hentAlleOppgaveFilterSettTilknyttetAvdeling(Long avdelingsId) {
        var listeTypedQuery = entityManager.createQuery("FROM OppgaveFiltrering l WHERE l.avdeling.id = :id " + OPPGAVEFILTRERING_SORTERING_NAVN,
            OppgaveFiltrering.class).setParameter("id", avdelingsId);//$NON-NLS-1$
        return listeTypedQuery.getResultList();
    }

    public Optional<OppgaveFiltrering> hentOppgaveFilterSett(Long listeId) {
        var listeTypedQuery = entityManager.createQuery("FROM OppgaveFiltrering l WHERE l.id = :id " + OPPGAVEFILTRERING_SORTERING_NAVN,
            OppgaveFiltrering.class).setParameter("id", listeId);
        return listeTypedQuery.getResultStream().findFirst();
    }

    public KøSortering hentSorteringForListe(Long listeId) {
        var listeTypedQuery = entityManager.createQuery("SELECT l.sortering FROM OppgaveFiltrering l WHERE l.id = :id ", KøSortering.class)
            .setParameter("id", listeId);
        return listeTypedQuery.getResultStream().findFirst().orElse(null);
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
        refresh(oppgaveEgenskap.getOppgave()); // todo: behov for denne?
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

    public void slettFiltreringBehandlingType(Long sakslisteId, BehandlingType behandlingType) {
        entityManager.createNativeQuery("""
                DELETE FROM FILTRERING_BEHANDLING_TYPE f
                WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.behandling_type = :behandlingType
                """).setParameter("oppgaveFiltreringId", sakslisteId)//$NON-NLS-1$ // NOSONAR
            .setParameter("behandlingType", behandlingType.getKode()).executeUpdate();
    }

    public <U extends BaseEntitet> void refresh(U entitet) {
        // todo: unødvendig? managed objects vil antakelig refreshes?
        entityManager.refresh(entitet);
    }

    public Oppgave opprettOppgave(Oppgave oppgave) {
        // todo: brukes bare i test, vurder om nødvendig
        lagre(oppgave);
        entityManager.refresh(oppgave);
        return oppgave;
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
            """, OppgaveEventLogg.class).setParameter(BEHANDLING_ID, behandlingId).getResultList();
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
            .setParameter(BEHANDLING_ID, behandlingId)
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

    public List<Oppgave> hentOppgaver(BehandlingId behandlingId) {
        return entityManager.createQuery("FROM Oppgave o where o.behandlingId = :behandlingId", Oppgave.class)
            .setParameter(BEHANDLING_ID, behandlingId)
            .getResultList();
    }

    public Optional<Oppgave> hentAktivOppgave(BehandlingId behandlingId) {
        var oppgaver = entityManager.createQuery("""
            FROM Oppgave o
            where o.behandlingId = :behandlingId
            and o.aktiv = :aktiv
            """, Oppgave.class).setParameter(BEHANDLING_ID, behandlingId).setParameter("aktiv", true).getResultList();
        if (oppgaver.size() > 1) {
            LOG.warn("Flere enn én aktive oppgaver for behandlingId {}", behandlingId);
        }
        return oppgaver.stream().max(Comparator.comparing(Oppgave::getOpprettetTidspunkt));
    }

}
