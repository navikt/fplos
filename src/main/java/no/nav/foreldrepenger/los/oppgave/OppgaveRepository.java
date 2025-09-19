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
import java.util.HashMap;
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
    private static final String BELØP = "o.belop";
    private static final String FEILUTBETALINGSTART = "o.feilutbetalingstart";
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

    private static String andreKriterierSubquery(Oppgavespørring queryDto, HashMap<String, Object> parameters) {
        var inkluderAkt = queryDto.getInkluderAndreKriterierTyper();
        var ekskluderAkt = queryDto.getEkskluderAndreKriterierTyper();

        var sb = new StringBuilder();
        if (!inkluderAkt.isEmpty()) {
            parameters.put("inkluderAktKoder", inkluderAkt);
            parameters.put("inkluderAktAntall", inkluderAkt.size());
            sb.append(" AND :inkluderAktAntall = (")
                .append("   SELECT COUNT(oe.andreKriterierType) ")
                .append("   FROM OppgaveEgenskap oe ")
                .append("   WHERE oe.oppgave = o ")
                .append("     AND oe.andreKriterierType IN (:inkluderAktKoder)")
                .append(" ) ");
        }
        if (!ekskluderAkt.isEmpty()) {
            parameters.put("ekskluderAktKoder", ekskluderAkt);
            sb.append("AND NOT EXISTS ( ")
                .append("SELECT 1 FROM OppgaveEgenskap oe ")
                .append("WHERE oe.oppgave = o AND oe.andreKriterierType IN (:ekskluderAktKoder)")
                .append(") ");
        }

        return sb.toString();
    }

    private static String filtrerBehandlingType(Oppgavespørring queryDto, HashMap<String, Object> parameters) {
        if (queryDto.getBehandlingTyper().isEmpty()) {
            return "";
        }
        parameters.put("behtyper", queryDto.getBehandlingTyper());
        return "AND o.behandlingType in :behtyper ";
    }

    private static String filtrerYtelseType(Oppgavespørring queryDto, HashMap<String, Object> parameters) {
        if (queryDto.getYtelseTyper().isEmpty()) {
            return "";
        }
        parameters.put("fagsakYtelseType", queryDto.getYtelseTyper());
        return "AND o.fagsakYtelseType in :fagsakYtelseType ";
    }

    private <T> TypedQuery<T> lagOppgavespørring(String selection, Class<T> resultClass, Oppgavespørring queryDto) {
        var parameters = new HashMap<String, Object>();
        parameters.put("enhetsnummer", queryDto.getEnhetsnummer());
        var sb = new StringBuilder();
        sb.append(selection);
        sb.append(" WHERE o.behandlendeEnhet = :enhetsnummer ");
        sb.append(filtrerBehandlingType(queryDto, parameters));
        sb.append(filtrerYtelseType(queryDto, parameters));
        sb.append(andreKriterierSubquery(queryDto, parameters));
        sb.append(reserverteSubquery(parameters));
        sb.append(tilBeslutter(queryDto, parameters));
        sb.append(" AND o.aktiv = true ");
        sb.append(sortering(selection, queryDto, parameters));

        //var query = entityManager.createQuery(selection + //$NON-NLS-1$ // NOSONAR
       //     " WHERE o.behandlendeEnhet = :enhetsnummer " + filtrerBehandlingType(queryDto) + filtrerYtelseType(queryDto)
       //     + andreKriterierSubquery(queryDto) + reserverteSubquery(queryDto) + tilBeslutter(queryDto)
       //     + "AND o.aktiv = true " + sortering(selection, queryDto), oppgaveClass);
        var query = entityManager.createQuery(sb.toString(), resultClass);
        parameters.forEach(query::setParameter);

        return query;
    }

    private String sortering(String selection, Oppgavespørring oppgavespørring, HashMap<String, Object> parameters) {
        if (selection.equals(COUNT_FRA_OPPGAVE) || selection.equals(COUNT_FRA_TILBAKEKREVING_OPPGAVE)) {
            return "";
        }
        var sortering = oppgavespørring.getSortering();
        if (FT_HELTALL.equalsIgnoreCase(oppgavespørring.getSortering().getFelttype())) {
            if (oppgavespørring.getFiltrerFra() != null) {
                parameters.put("filterFra", BigDecimal.valueOf(oppgavespørring.getFiltrerFra()));
            }
            if (oppgavespørring.getFiltrerTil() != null) {
                parameters.put("filterTil", BigDecimal.valueOf(oppgavespørring.getFiltrerTil()));
            }
        } else if (FT_DATO.equalsIgnoreCase(oppgavespørring.getSortering().getFelttype())) {
            if (oppgavespørring.getFiltrerFra() != null) {
                parameters.put("filterFomDager", KøSortering.FØRSTE_STØNADSDAG.equals(oppgavespørring.getSortering()) ? LocalDate.now()
                    .plusDays(oppgavespørring.getFiltrerFra()) : LocalDateTime.now().plusDays(oppgavespørring.getFiltrerFra()).with(LocalTime.MIN));
            }
            if (oppgavespørring.getFiltrerTil() != null) {
                parameters.put("filterTomDager", KøSortering.FØRSTE_STØNADSDAG.equals(oppgavespørring.getSortering()) ? LocalDate.now()
                    .plusDays(oppgavespørring.getFiltrerTil()) : LocalDateTime.now().plusDays(oppgavespørring.getFiltrerTil()).with(LocalTime.MAX));
            }
            if (oppgavespørring.getFiltrerFomDato() != null) {
                parameters.put("filterFomDato", KøSortering.FØRSTE_STØNADSDAG.equals(
                    oppgavespørring.getSortering()) ? oppgavespørring.getFiltrerFomDato() : oppgavespørring.getFiltrerFomDato()
                    .atTime(LocalTime.MIN));
            }
            if (oppgavespørring.getFiltrerTomDato() != null) {
                parameters.put("filterTomDato", KøSortering.FØRSTE_STØNADSDAG.equals(
                    oppgavespørring.getSortering()) ? oppgavespørring.getFiltrerTomDato() : oppgavespørring.getFiltrerTomDato()
                    .atTime(LocalTime.MAX));
            }
        }

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

    private static String reserverteSubquery(HashMap<String, Object> parameters) {
        parameters.put("nå", LocalDateTime.now());
        return "AND NOT EXISTS (select r from Reservasjon r where r.oppgave = o and r.reservertTil > :nå) ";
    }

    private static String tilBeslutter(Oppgavespørring dto, HashMap<String, Object> parameters) {
        var tilBeslutterKø = dto.getInkluderAndreKriterierTyper().contains(AndreKriterierType.TIL_BESLUTTER);
        if (dto.getForAvdelingsleder() || !tilBeslutterKø) {
            return "";
        }
        parameters.put("tilbeslutter", AndreKriterierType.TIL_BESLUTTER);
        parameters.put("uid", BrukerIdent.brukerIdent());
        return """
            AND NOT EXISTS (
                select oetilbesl.oppgave from OppgaveEgenskap oetilbesl
                where oetilbesl.oppgave = o
                    AND oetilbesl.andreKriterierType = :tilbeslutter
                    AND upper(oetilbesl.sisteSaksbehandlerForTotrinn) = :uid
            )""";
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
