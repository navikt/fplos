package no.nav.foreldrepenger.los.oppgave;


import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringBehandlingType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringOppdaterer;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonEventLogg;

import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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

import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.FT_DATO;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.FT_HELTALL;

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
    private static final String FORSTE_STONADSDAG = "o.forsteStonadsdag";
    private static final String BELOP = "o.belop";
    private static final String FEILUTBETALINGSTART = "o.feilutbetalingstart";
    private static final String OPPGAVEFILTRERING_SORTERING_NAVN = "ORDER BY l.navn";

    private EntityManager entityManager;

    @Inject
    public OppgaveRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OppgaveRepository() {
    }

    public int hentAntallOppgaver(Oppgavespørring oppgavespørring) {
        var selection = COUNT_FRA_OPPGAVE;
        if (oppgavespørring.getSortering() != null) {
            switch (oppgavespørring.getSortering().getFeltkategori()) {
                case KøSortering.FK_TILBAKEKREVING -> selection = COUNT_FRA_TILBAKEKREVING_OPPGAVE;
                case KøSortering.FK_UNIVERSAL -> selection = COUNT_FRA_OPPGAVE;
            }
        }
        var oppgaveTypedQuery = lagOppgavespørring(selection, Long.class, oppgavespørring);
        return oppgaveTypedQuery.getSingleResult().intValue();
    }

    public int hentAntallOppgaverForAvdeling(Long avdelingsId) {
        var oppgavespørring = new Oppgavespørring(avdelingsId, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null, null, null);
        var oppgaveTypedQuery = lagOppgavespørring(COUNT_FRA_OPPGAVE, Long.class, oppgavespørring);
        return oppgaveTypedQuery.getSingleResult().intValue();
    }

    public List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring) {
        return hentOppgaver(oppgavespørring, 0);
    }

    public List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring, int maksAntall) {
        var selection = SELECT_FRA_OPPGAVE;
        if (oppgavespørring.getSortering() != null) {
            switch (oppgavespørring.getSortering().getFeltkategori()) {
                case KøSortering.FK_TILBAKEKREVING -> selection = SELECT_FRA_TILBAKEKREVING_OPPGAVE;
                case KøSortering.FK_UNIVERSAL -> selection = SELECT_FRA_OPPGAVE;
            }
        }
        var oppgaveTypedQuery = lagOppgavespørring(selection, Oppgave.class, oppgavespørring);
        if (maksAntall > 0) {
            oppgaveTypedQuery.setMaxResults(maksAntall);
        }
        return oppgaveTypedQuery.getResultList();
    }

    private <T> TypedQuery<T> lagOppgavespørring(String selection, Class<T> oppgaveClass, Oppgavespørring queryDto) {
        var filtrerBehandlingType = queryDto.getBehandlingTyper().isEmpty() ? "" : "AND o.behandlingType in :behtyper ";
        var filtrerYtelseType = queryDto.getYtelseTyper()
                .isEmpty() ? "" : "AND o.fagsakYtelseType in :fagsakYtelseType ";

        var ekskluderInkluderAndreKriterier = new StringBuilder();
        for (var kriterie : queryDto.getInkluderAndreKriterierTyper()) {
            ekskluderInkluderAndreKriterier.append(
                    "AND EXISTS ( SELECT  1 FROM OppgaveEgenskap oe WHERE o = oe.oppgave AND oe.aktiv = true AND oe.andreKriterierType = '")
                    .append(kriterie.getKode())
                    .append("' ) ");
        }
        for (var kriterie : queryDto.getEkskluderAndreKriterierTyper()) {
            ekskluderInkluderAndreKriterier.append(
                    "AND NOT EXISTS (select 1 from OppgaveEgenskap oen WHERE o = oen.oppgave AND oen.aktiv = true AND oen.andreKriterierType = '")
                    .append(kriterie.getKode())
                    .append("') ");
        }

        var query = entityManager.createQuery(selection + //$NON-NLS-1$ // NOSONAR
                "INNER JOIN avdeling a ON a.avdelingEnhet = o.behandlendeEnhet " + "WHERE 1=1 " + filtrerBehandlingType
                + filtrerYtelseType + ekskluderInkluderAndreKriterier
                + avgrensPåAktiveReservasjoner(queryDto)
                + tilBeslutter(queryDto) + avgrenseTilOppgaveId(queryDto) + "AND a.id = :enhet " + "AND o.aktiv = true "
                + sortering(queryDto), oppgaveClass)
                .setParameter("enhet", queryDto.getEnhetId());
        if (!queryDto.ignorerReserversjoner()) {
            query.setParameter("nå", LocalDateTime.now());
        }
        if (!queryDto.getForAvdelingsleder()) {
            query.setParameter("tilbeslutter", AndreKriterierType.TIL_BESLUTTER).setParameter("uid", finnBrukernavn());
        }
        if (!queryDto.getBehandlingTyper().isEmpty()) {
            query.setParameter("behtyper", queryDto.getBehandlingTyper());
        }
        if (!queryDto.getYtelseTyper().isEmpty()) {
            query.setParameter("fagsakYtelseType", queryDto.getYtelseTyper());
        }
        if (queryDto.getSortering() != null) {
            if (FT_HELTALL.equalsIgnoreCase(queryDto.getSortering().getFelttype())) {
                if (queryDto.getFiltrerFra() != null) {
                    query.setParameter("filterFra", BigDecimal.valueOf(queryDto.getFiltrerFra()));
                }
                if (queryDto.getFiltrerTil() != null) {
                    query.setParameter("filterTil", BigDecimal.valueOf(queryDto.getFiltrerTil()));
                }
            } else if (FT_DATO.equalsIgnoreCase(queryDto.getSortering().getFelttype())) {
                if (queryDto.getFiltrerFra() != null) {
                    query.setParameter("filterFomDager",
                            KøSortering.FORSTE_STONADSDAG.equals(queryDto.getSortering()) ? LocalDate.now()
                                    .plusDays(queryDto.getFiltrerFra()) : LocalDateTime.now()
                                    .plusDays(queryDto.getFiltrerFra())
                                    .with(LocalTime.MIN));
                }
                if (queryDto.getFiltrerTil() != null) {
                    query.setParameter("filterTomDager",
                            KøSortering.FORSTE_STONADSDAG.equals(queryDto.getSortering()) ? LocalDate.now()
                                    .plusDays(queryDto.getFiltrerTil()) : LocalDateTime.now()
                                    .plusDays(queryDto.getFiltrerTil())
                                    .with(LocalTime.MAX));
                }
                if (queryDto.getFiltrerFomDato() != null) {
                    query.setParameter("filterFomDato", KøSortering.FORSTE_STONADSDAG.equals(
                            queryDto.getSortering()) ? queryDto.getFiltrerFomDato() : queryDto.getFiltrerFomDato()
                            .atTime(LocalTime.MIN));
                }
                if (queryDto.getFiltrerTomDato() != null) {
                    query.setParameter("filterTomDato", KøSortering.FORSTE_STONADSDAG.equals(
                            queryDto.getSortering()) ? queryDto.getFiltrerTomDato() : queryDto.getFiltrerTomDato()
                            .atTime(LocalTime.MAX));
                }
            }
        }

        return query;
    }

    private String avgrensPåAktiveReservasjoner(Oppgavespørring queryDto) {
        return queryDto.ignorerReserversjoner() ? "" : "AND NOT EXISTS (select r from Reservasjon r where r.oppgave = o and r.reservertTil > :nå) ";
    }

    private String avgrenseTilOppgaveId(Oppgavespørring queryDto) {
        return queryDto.getAvgrenseTilOppgaveId()
                .map(oppgaveId -> String.format("AND o.id = %s ", oppgaveId))
                .orElse("");
    }

    private String tilBeslutter(Oppgavespørring dto) {
        return dto.getForAvdelingsleder() ? "" :
                "AND NOT EXISTS (select oetilbesl.oppgave from OppgaveEgenskap oetilbesl "
                        + "where oetilbesl.oppgave = o AND oetilbesl.aktiv = true AND oetilbesl.andreKriterierType = :tilbeslutter "
                        + "AND upper(oetilbesl.sisteSaksbehandlerForTotrinn) = upper( :uid ) ) ";
    }

    private String sortering(Oppgavespørring oppgavespørring) {
        var sortering = oppgavespørring.getSortering();
        if (KøSortering.BEHANDLINGSFRIST.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(BEHANDLINGSFRIST,
                    oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil()) : filtrerStatisk(BEHANDLINGSFRIST,
                    oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.OPPRETT_BEHANDLING.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(BEHANDLINGOPPRETTET,
                    oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil()) : filtrerStatisk(
                    BEHANDLINGOPPRETTET, oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.FORSTE_STONADSDAG.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(FORSTE_STONADSDAG,
                    oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil()) : filtrerStatisk(
                    FORSTE_STONADSDAG, oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
        }
        if (KøSortering.BELOP.equals(sortering)) {
            return filtrerNumerisk(BELOP, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil());
        }
        if (KøSortering.FEILUTBETALINGSTART.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode() ? filtrerDynamisk(FEILUTBETALINGSTART,
                    oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil()) : filtrerStatisk(
                    FEILUTBETALINGSTART, oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
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
            datoFiltrering = "AND " + sortering + " > :filterFomDato AND " + sortering + " < :filterTomDato ";
        } else if (fomDato != null) {
            datoFiltrering = "AND " + sortering + " > :filterFomDato ";
        } else if (tomDato != null) {
            datoFiltrering = "AND " + sortering + " < :filterTomDato ";
        }
        return datoFiltrering + SORTERING + sortering;
    }

    private static String finnBrukernavn() {
        var brukerident = SubjectHandler.getSubjectHandler().getUid();
        return brukerident
                != null ? brukerident.toUpperCase() : BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }

    public List<Reservasjon> hentReservasjonerTilknyttetAktiveOppgaver(String uid) {
        var oppgaveTypedQuery = entityManager.createQuery(
                "Select r from Reservasjon r " + "INNER JOIN Oppgave o ON r.oppgave = o "
                        + "WHERE r.reservertTil > :nå AND upper(r.reservertAv) = upper( :uid ) AND o.aktiv = true",
                Reservasjon.class) //$NON-NLS-1$
                .setParameter("nå", LocalDateTime.now()).setParameter("uid", uid);
        return oppgaveTypedQuery.getResultList();
    }

    public List<Reservasjon> hentAlleReservasjonerForAvdeling(String avdelingEnhet) {
        var listeTypedQuery = entityManager.createQuery(
                "Select r FROM Reservasjon r INNER JOIN Oppgave o ON r.oppgave = o "
                        + "WHERE r.reservertTil > :nå AND o.behandlendeEnhet = :behandlendeEnhet "
                        + "ORDER BY r.reservertAv", Reservasjon.class)
                .setParameter("nå", LocalDateTime.now())
                .setParameter("behandlendeEnhet", avdelingEnhet);
        return listeTypedQuery.getResultList();
    }

    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe) {
        return entityManager.createQuery(
                SELECT_FRA_OPPGAVE + "WHERE o.fagsakSaksnummer in :fagsakSaksnummerListe " + "AND o.aktiv = true "
                        + "ORDER BY o.fagsakSaksnummer desc ", Oppgave.class)
                .setParameter("fagsakSaksnummerListe", fagsakSaksnummerListe)
                .getResultList();
    }

    public Reservasjon hentReservasjon(Long oppgaveId) {
        var query = entityManager.createQuery("from Reservasjon r WHERE r.oppgave.id = :id ", Reservasjon.class)
                .setParameter("id", oppgaveId);//$NON-NLS-1$
        var resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return new Reservasjon(entityManager.find(Oppgave.class, oppgaveId));
        }
        return query.getResultList().get(0);
    }

    public List<OppgaveFiltrering> hentAlleOppgaveFilterSettTilknyttetAvdeling(Long avdelingsId) {
        var listeTypedQuery = entityManager.createQuery(
                "FROM OppgaveFiltrering l WHERE l.avdeling.id = :id " + OPPGAVEFILTRERING_SORTERING_NAVN,
                OppgaveFiltrering.class).setParameter("id", avdelingsId);//$NON-NLS-1$
        return listeTypedQuery.getResultList();
    }

    public Optional<OppgaveFiltrering> hentOppgaveFilterSett(Long listeId) {
        var listeTypedQuery = entityManager.createQuery(
                "FROM OppgaveFiltrering l WHERE l.id = :id " + OPPGAVEFILTRERING_SORTERING_NAVN,
                OppgaveFiltrering.class).setParameter("id", listeId);
        return listeTypedQuery.getResultStream().findFirst();
    }

    public KøSortering hentSorteringForListe(Long listeId) {
        var listeTypedQuery = entityManager.createQuery("SELECT l.sortering FROM OppgaveFiltrering l WHERE l.id = :id ",
                KøSortering.class).setParameter("id", listeId);
        return listeTypedQuery.getResultStream().findFirst().orElse(null);
    }

    public void lagre(Reservasjon reservasjon) {
        internLagre(reservasjon);
    }

    public void lagre(Oppgave oppgave) {
        internLagre(oppgave);
    }

    public void lagre(TilbakekrevingOppgave egenskaper) {
        internLagre(egenskaper);
    }

    public void lagre(FiltreringBehandlingType filtreringBehandlingType) {
        internLagre(filtreringBehandlingType);
    }

    public void lagre(FiltreringYtelseType filtreringYtelseType) {
        internLagre(filtreringYtelseType);
    }

    public void lagre(FiltreringAndreKriterierType filtreringAndreKriterierType) {
        internLagre(filtreringAndreKriterierType);
    }

    public Long lagre(OppgaveFiltrering oppgaveFiltrering) {
        internLagre(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    public void oppdaterNavn(Long sakslisteId, String navn) {
        entityManager.persist(entityManager.find(OppgaveFiltreringOppdaterer.class, sakslisteId).endreNavn(navn));
        entityManager.flush();
    }

    public void slettListe(Long listeId) {
        entityManager.remove(entityManager.find(OppgaveFiltrering.class, listeId));
        entityManager.flush();
    }

    public void slettFiltreringBehandlingType(Long sakslisteId, BehandlingType behandlingType) {
        entityManager.createNativeQuery("DELETE FROM FILTRERING_BEHANDLING_TYPE f "
                + "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.behandling_type = :behandlingType")
                .setParameter("oppgaveFiltreringId", sakslisteId)//$NON-NLS-1$ // NOSONAR
                .setParameter("behandlingType", behandlingType.getKode())
                .executeUpdate();
    }

    public void slettFiltreringYtelseType(Long sakslisteId, FagsakYtelseType fagsakYtelseType) {
        entityManager.createNativeQuery("DELETE FROM FILTRERING_YTELSE_TYPE f "
                + "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.FAGSAK_YTELSE_TYPE = :fagsakYtelseType")
                .setParameter("oppgaveFiltreringId", sakslisteId)//$NON-NLS-1$ // NOSONAR
                .setParameter("fagsakYtelseType", fagsakYtelseType.getKode())
                .executeUpdate();
    }

    public void slettFiltreringAndreKriterierType(Long oppgavefiltreringId, AndreKriterierType andreKriterierType) {
        entityManager.createNativeQuery("DELETE FROM FILTRERING_ANDRE_KRITERIER f "
                + "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.ANDRE_KRITERIER_TYPE = :andreKriterierType")
                .setParameter("oppgaveFiltreringId", oppgavefiltreringId)//$NON-NLS-1$ // NOSONAR
                .setParameter("andreKriterierType", andreKriterierType.getKode())
                .executeUpdate();
    }

    public void refresh(Oppgave oppgave) {
        entityManager.refresh(oppgave);
    }

    public void refresh(OppgaveFiltrering oppgaveFiltrering) {
        entityManager.refresh(oppgaveFiltrering);
    }

    public void refresh(Avdeling avdeling) {
        entityManager.refresh(avdeling);
    }

    public void refresh(Saksbehandler saksbehandler) {
        entityManager.refresh(saksbehandler);
    }

    public List<Oppgave> sjekkOmOppgaverFortsattErTilgjengelige(List<Long> oppgaveIder) {
        return entityManager.createQuery(
                SELECT_FRA_OPPGAVE + " INNER JOIN avdeling a ON a.avdelingEnhet = o.behandlendeEnhet WHERE "
                        + "NOT EXISTS (select r from Reservasjon r where r.oppgave = o and r.reservertTil > :nå) "
                        + "AND o.id IN ( :oppgaveId ) " + "AND o.aktiv = true", Oppgave.class) //$NON-NLS-1$
                .setParameter("nå", LocalDateTime.now()).setParameter("oppgaveId", oppgaveIder).getResultList();

    }

    public Oppgave opprettOppgave(Oppgave oppgave) {
        internLagre(oppgave);
        entityManager.refresh(oppgave);
        return oppgave;
    }

    public TilbakekrevingOppgave opprettTilbakekrevingOppgave(TilbakekrevingOppgave oppgave) {
        internLagre(oppgave);
        entityManager.refresh(oppgave);
        return oppgave;
    }

    public Optional<TilbakekrevingOppgave> hentAktivTilbakekrevingOppgave(BehandlingId behandlingId) {
        return hentOppgaver(behandlingId, TilbakekrevingOppgave.class).stream().filter(Oppgave::getAktiv).findFirst();
    }

    public Optional<Oppgave> gjenåpneOppgaveForBehandling(BehandlingId behandlingId) {
        var sisteOppgave = hentOppgaver(behandlingId, Oppgave.class).stream()
                .max(Comparator.comparing(Oppgave::getOpprettetTidspunkt));
        sisteOppgave.ifPresent(o -> {
            if (o.getAktiv()) {
                LOG.info(String.format("Forsøker gjenåpning av allerede aktiv oppgaveId %s", o.getId()));
            }
            o.gjenåpneOppgave();
            internLagre(o);
            entityManager.refresh(o);
        });
        return sisteOppgave;
    }

    public TilbakekrevingOppgave gjenåpneTilbakekrevingOppgave(BehandlingId behandlingId) {
        var oppgaver = hentOppgaver(behandlingId, TilbakekrevingOppgave.class);
        var sisteOppgave = oppgaver.stream().max(Comparator.comparing(Oppgave::getOpprettetTidspunkt)).orElse(null);
        if (sisteOppgave != null) {
            sisteOppgave.gjenåpneOppgave();
            internLagre(sisteOppgave);
            entityManager.refresh(sisteOppgave);
        }
        return sisteOppgave;
    }

    public void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        var oppgaver = hentOppgaver(behandlingId, Oppgave.class);
        var antallAktive = oppgaver.stream().filter(Oppgave::getAktiv).count();
        if (antallAktive > 1) {
            throw new IllegalStateException(
                    String.format("Forventet kun én aktiv oppgave for behandlingId %s, fant %s", behandlingId,
                            antallAktive));
        }
        oppgaver.stream()
                .filter(Oppgave::getAktiv)
                .max(Comparator.comparing(Oppgave::getOpprettetTidspunkt))
                .ifPresent(oppgave -> {
                    frigiEventuellReservasjon(oppgave.getReservasjon());
                    oppgave.avsluttOppgave();
                    internLagre(oppgave);
                    entityManager.refresh(oppgave);
                });
    }

    private void frigiEventuellReservasjon(Reservasjon reservasjon) {
        if (reservasjon != null && reservasjon.erAktiv()) {
            reservasjon.frigiReservasjon("Oppgave avsluttet");
            lagre(reservasjon);
            lagre(new ReservasjonEventLogg(reservasjon));
        }
    }

    public List<Oppgave> hentSisteReserverteOppgaver(String uid) {
        return entityManager.createQuery("SELECT o FROM Oppgave o " + "INNER JOIN Reservasjon r ON r.oppgave = o "
                        + "WHERE upper(r.reservertAv) = upper( :uid ) ORDER BY coalesce(r.endretTidspunkt, r.opprettetTidspunkt) DESC ",
                Oppgave.class) //$NON-NLS-1$
                .setParameter("uid", uid).setMaxResults(10).getResultList();
    }

    public void lagre(OppgaveEgenskap oppgaveEgenskap) {
        internLagre(oppgaveEgenskap);
        refresh(oppgaveEgenskap.getOppgave());
    }

    public List<OppgaveEventLogg> hentOppgaveEventer(BehandlingId behandlingId) {
        Objects.requireNonNull(behandlingId, "behandlingId kan ikke være null");
        return entityManager.createQuery("FROM oppgaveEventLogg oel " + "where oel.behandlingId = :behandlingId "
                + "order by oel.opprettetTidspunkt desc", OppgaveEventLogg.class)
                .setParameter("behandlingId", behandlingId)
                .getResultList();
    }

    public List<OppgaveEgenskap> hentOppgaveEgenskaper(Long oppgaveId) {
        return entityManager.createQuery(
                "FROM OppgaveEgenskap oe " + "where oe.oppgaveId = :oppgaveId ORDER BY oe.id desc",
                OppgaveEgenskap.class).setParameter("oppgaveId", oppgaveId).getResultList();
    }

    public void lagre(OppgaveEventLogg oppgaveEventLogg) {
        internLagre(oppgaveEventLogg);
    }

    public void lagre(ReservasjonEventLogg reservasjonEventLogg) {
        internLagre(reservasjonEventLogg);
    }

    private <T> List<T> hentOppgaver(BehandlingId behandlingId, Class<T> cls) {
        var select = cls.equals(TilbakekrevingOppgave.class) ? SELECT_FRA_TILBAKEKREVING_OPPGAVE : SELECT_FRA_OPPGAVE;
        return entityManager.createQuery(select + "WHERE o.behandlingId = :behandlingId ", cls)
                .setParameter("behandlingId", behandlingId)
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
                .endreFomDato(fomDato)
                .endreTomDato(tomDato));
        entityManager.flush();
    }

    public void settSorteringNumeriskIntervall(Long oppgaveFiltreringId, Long fra, Long til) {
        entityManager.persist(entityManager.find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
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

    public List<Oppgave> hentOppgaverForSynkronisering() {
        return entityManager.createQuery("FROM Oppgave o where o.aktiv = true AND o.system = :system", Oppgave.class)
                .setParameter("system", "FPSAK")
                .getResultList();
    }

    public Oppgave hentOppgave(Long oppgaveId) {
        return entityManager.createQuery("FROM Oppgave o where o.id = :id", Oppgave.class)
                .setParameter("id", oppgaveId)
                .getSingleResult();
    }

    public List<Oppgave> hentOppgaver(BehandlingId behandlingId) {
        return entityManager.createQuery("FROM Oppgave o where o.behandlingId = :behandlingId", Oppgave.class)
                .setParameter("behandlingId", behandlingId)
                .getResultList();
    }

    private void internLagre(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }
}
