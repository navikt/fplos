package no.nav.foreldrepenger.loslager.repository;

import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
import static no.nav.foreldrepenger.loslager.oppgave.KøSortering.FT_DATO;
import static no.nav.foreldrepenger.loslager.oppgave.KøSortering.FT_HELTALL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringBehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltreringOppdaterer;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.ReservasjonEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

@ApplicationScoped
public class OppgaveRepositoryImpl implements OppgaveRepository {

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
    public OppgaveRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OppgaveRepositoryImpl(){
    }

    @Override
    public int hentAntallOppgaver(Oppgavespørring oppgavespørring) {
        String selection = COUNT_FRA_OPPGAVE;
        if(oppgavespørring.getSortering() != null) {
            switch (oppgavespørring.getSortering().getFeltkategori()) {
                case KøSortering.FK_TILBAKEKREVING:
                    selection = COUNT_FRA_TILBAKEKREVING_OPPGAVE;
                    break;
                case KøSortering.FK_UNIVERSAL:
                    selection = COUNT_FRA_OPPGAVE;
                    break;
            }
        }
        TypedQuery<Long> oppgaveTypedQuery = lagOppgavespørring(selection, Long.class, oppgavespørring);
        return oppgaveTypedQuery.getSingleResult().intValue();
    }

    @Override
    public int hentAntallOppgaverForAvdeling(Long avdelingsId) {
        Oppgavespørring oppgavespørring = new Oppgavespørring(avdelingsId,KøSortering.BEHANDLINGSFRIST,new ArrayList<>(), new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),false,null,null,null,null);
        TypedQuery<Long> oppgaveTypedQuery = lagOppgavespørring(COUNT_FRA_OPPGAVE, Long.class, oppgavespørring);
        return oppgaveTypedQuery.getSingleResult().intValue();
    }

    @Override
    public List<Oppgave> hentOppgaver(Oppgavespørring oppgavespørring) {
        String selection = SELECT_FRA_OPPGAVE;
        if(oppgavespørring.getSortering() != null) {
            switch (oppgavespørring.getSortering().getFeltkategori()) {
                case KøSortering.FK_TILBAKEKREVING:
                    selection = SELECT_FRA_TILBAKEKREVING_OPPGAVE;
                    break;
                case KøSortering.FK_UNIVERSAL:
                    selection = SELECT_FRA_OPPGAVE;
                    break;
            }
        }
        TypedQuery<Oppgave> oppgaveTypedQuery = lagOppgavespørring(selection, Oppgave.class, oppgavespørring);
        return oppgaveTypedQuery.getResultList();
    }

    private <T> TypedQuery<T> lagOppgavespørring(String selection, Class<T> oppgaveClass, Oppgavespørring queryDto) {
        String filtrerBehandlingType = queryDto.getBehandlingTyper().isEmpty() ? "": " o.behandlingType in :behtyper AND ";
        String filtrerYtelseType = queryDto.getYtelseTyper().isEmpty() ? "": " o.fagsakYtelseType in :fagsakYtelseType AND ";

        StringBuilder ekskluderInkluderAndreKriterier = new StringBuilder();
        for (var kriterie : queryDto.getInkluderAndreKriterierTyper()) {
            ekskluderInkluderAndreKriterier.append("EXISTS ( SELECT  1 FROM OppgaveEgenskap oe WHERE o = oe.oppgave AND oe.aktiv = true AND oe.andreKriterierType = '" + kriterie.getKode() + "' ) AND ");
        }
        for (var kriterie : queryDto.getEkskluderAndreKriterierTyper()) {
            ekskluderInkluderAndreKriterier.append("NOT EXISTS (select 1 from OppgaveEgenskap oen WHERE o = oen.oppgave AND oen.aktiv = true AND oen.andreKriterierType = '").append(kriterie.getKode()).append("') AND ");
        }

        TypedQuery<T> query = entityManager.createQuery(selection + //$NON-NLS-1$ // NOSONAR
                "INNER JOIN avdeling a ON a.avdelingEnhet = o.behandlendeEnhet " +
                "WHERE " +
                filtrerBehandlingType +
                filtrerYtelseType +
                ekskluderInkluderAndreKriterier +
                "NOT EXISTS (select r from Reservasjon r where r.oppgave = o and r.reservertTil > :naa) " +
                tilBeslutter(queryDto) +
                "AND a.id = :enhet " +
                "AND o.aktiv = true " + sortering(queryDto), oppgaveClass)
                .setParameter("naa", LocalDateTime.now())
                .setParameter("enhet", queryDto.getId());

        if (!queryDto.getForAvdelingsleder()) {
            query.setParameter("tilbeslutter", AndreKriterierType.TIL_BESLUTTER)
                    .setParameter("uid", finnBrukernavn());
        }
        if (!queryDto.getBehandlingTyper().isEmpty()) {
            query.setParameter("behtyper", queryDto.getBehandlingTyper());
        }
        if (!queryDto.getYtelseTyper().isEmpty()) {
            query.setParameter("fagsakYtelseType", queryDto.getYtelseTyper());
        }
        if(queryDto.getSortering() != null) {
            if (FT_HELTALL.equalsIgnoreCase(queryDto.getSortering().getFelttype())) {
                if (queryDto.getFiltrerFra() != null) {
                    query.setParameter("filterFra", BigDecimal.valueOf(queryDto.getFiltrerFra()));
                }
                if (queryDto.getFiltrerTil() != null) {
                    query.setParameter("filterTil", BigDecimal.valueOf(queryDto.getFiltrerTil()));
                }
            } else if (FT_DATO.equalsIgnoreCase(queryDto.getSortering().getFelttype())) {
                if (queryDto.getFiltrerFra() != null) {
                    query.setParameter("filterFomDager", KøSortering.FORSTE_STONADSDAG.equals(queryDto.getSortering()) ? LocalDate.now().plusDays(queryDto.getFiltrerFra()) : LocalDateTime.now().plusDays(queryDto.getFiltrerFra()).with(LocalTime.MIN));
                }
                if (queryDto.getFiltrerTil() != null) {
                    query.setParameter("filterTomDager", KøSortering.FORSTE_STONADSDAG.equals(queryDto.getSortering()) ? LocalDate.now().plusDays(queryDto.getFiltrerTil()) : LocalDateTime.now().plusDays(queryDto.getFiltrerTil()).with(LocalTime.MAX));
                }
                if (queryDto.getFiltrerFomDato() != null) {
                    query.setParameter("filterFomDato", KøSortering.FORSTE_STONADSDAG.equals(queryDto.getSortering()) ? queryDto.getFiltrerFomDato() : queryDto.getFiltrerFomDato().atTime(LocalTime.MIN));
                }
                if (queryDto.getFiltrerTomDato() != null) {
                    query.setParameter("filterTomDato", KøSortering.FORSTE_STONADSDAG.equals(queryDto.getSortering()) ? queryDto.getFiltrerTomDato() : queryDto.getFiltrerTomDato().atTime(LocalTime.MAX));
                }
            }
        }

        return query;
    }

    private String tilBeslutter(Oppgavespørring dto) {
        return dto.getForAvdelingsleder() ? ""
                : "AND NOT EXISTS (select oetilbesl.oppgave from OppgaveEgenskap oetilbesl " +
                "where oetilbesl.oppgave = o AND oetilbesl.aktiv = true AND oetilbesl.andreKriterierType = :tilbeslutter " +
                "AND upper(oetilbesl.sisteSaksbehandlerForTotrinn) = upper( :uid ) ) ";
    }

    private String sortering(Oppgavespørring oppgavespørring) {
        KøSortering sortering = oppgavespørring.getSortering();
        if (KøSortering.BEHANDLINGSFRIST.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode()
                    ? filtrerDynamisk(BEHANDLINGSFRIST, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil())
                    : filtrerStatisk(BEHANDLINGSFRIST, oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
        } else if (KøSortering.OPPRETT_BEHANDLING.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode()
                    ? filtrerDynamisk(BEHANDLINGOPPRETTET, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil())
                    : filtrerStatisk(BEHANDLINGOPPRETTET, oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
        } else if (KøSortering.FORSTE_STONADSDAG.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode()
                    ? filtrerDynamisk(FORSTE_STONADSDAG, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil())
                    : filtrerStatisk(FORSTE_STONADSDAG, oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
        } else if (KøSortering.BELOP.equals(sortering)) {
            return filtrerNumerisk(BELOP, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil());
        } else if (KøSortering.FEILUTBETALINGSTART.equals(sortering)) {
            return oppgavespørring.isErDynamiskPeriode()
                    ? filtrerDynamisk(FEILUTBETALINGSTART, oppgavespørring.getFiltrerFra(), oppgavespørring.getFiltrerTil())
                    : filtrerStatisk(FEILUTBETALINGSTART, oppgavespørring.getFiltrerFomDato(), oppgavespørring.getFiltrerTomDato());
        } else {
            return SORTERING + BEHANDLINGOPPRETTET;
        }
    }

    private String filtrerNumerisk(String sortering, Long fra, Long til) {
        String numeriskFiltrering = "";
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
        String datoFiltrering = "";
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
        String datoFiltrering = "";
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
        String brukerident = SubjectHandler.getSubjectHandler().getUid();
        return brukerident != null ? brukerident.toUpperCase() : BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }

    @Override
    public List<Reservasjon> hentReservasjonerTilknyttetAktiveOppgaver(String uid){
        TypedQuery<Reservasjon> oppgaveTypedQuery = entityManager.createQuery("Select r from Reservasjon r " +
                "INNER JOIN Oppgave o ON r.oppgave = o " +
                "WHERE r.reservertTil > :naa AND upper(r.reservertAv) = upper( :uid ) AND o.aktiv = true", Reservasjon.class) //$NON-NLS-1$
                .setParameter("naa", LocalDateTime.now())
                .setParameter("uid", uid);
        return oppgaveTypedQuery.getResultList();
    }

    @Override
    public List<Reservasjon> hentAlleReservasjonerForAvdeling(String avdelingEnhet) {
        TypedQuery<Reservasjon> listeTypedQuery = entityManager
                .createQuery("Select r FROM Reservasjon r INNER JOIN Oppgave o ON r.oppgave = o " +
                        "WHERE r.reservertTil > :naa AND o.behandlendeEnhet = :behandlendeEnhet " +
                        "ORDER BY r.reservertAv", Reservasjon.class)
                .setParameter("naa", LocalDateTime.now())
                .setParameter("behandlendeEnhet" ,avdelingEnhet);
        return listeTypedQuery.getResultList();
    }

    @Override
    public List<Oppgave> hentOppgaverForSaksnummer(Long fagsakSaksnummer) {
        return entityManager.createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.fagsakSaksnummer = :fagsakSaksnummer " +
                "ORDER BY o.id desc ", Oppgave.class)
                .setParameter("fagsakSaksnummer", fagsakSaksnummer)
                .getResultList();
    }

    @Override
    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe) {
        return entityManager.createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.fagsakSaksnummer in :fagsakSaksnummerListe " +
                "AND o.aktiv = true " +
                "ORDER BY o.fagsakSaksnummer desc ", Oppgave.class)
                .setParameter("fagsakSaksnummerListe", fagsakSaksnummerListe)
                .getResultList();
    }

    public Reservasjon hentReservasjon(Long oppgaveId){
        TypedQuery<Reservasjon> oppgaveTypedQuery =  entityManager.createQuery("from Reservasjon r WHERE r.oppgave.id = :id ", Reservasjon.class)
            .setParameter("id", oppgaveId);//$NON-NLS-1$
        List<Reservasjon> resultList = oppgaveTypedQuery.getResultList();
        if (resultList.isEmpty()){
            return new Reservasjon(entityManager.find(Oppgave.class, oppgaveId));
        }
        return oppgaveTypedQuery.getResultList().get(0);
    }

    @Override
    public List<OppgaveFiltrering> hentAlleFiltreringer(Long avdelingsId) {
        TypedQuery<OppgaveFiltrering> listeTypedQuery = entityManager
                .createQuery("FROM OppgaveFiltrering l WHERE l.avdeling.id = :id " +
                        OPPGAVEFILTRERING_SORTERING_NAVN, OppgaveFiltrering.class)
                .setParameter("id", avdelingsId);//$NON-NLS-1$
        return listeTypedQuery.getResultList();
    }

    @Override
    public OppgaveFiltrering hentFiltrering(Long listeId) {
        TypedQuery<OppgaveFiltrering> listeTypedQuery = entityManager
                .createQuery("FROM OppgaveFiltrering l WHERE l.id = :id " +
                        OPPGAVEFILTRERING_SORTERING_NAVN, OppgaveFiltrering.class)
                .setParameter("id", listeId);
        return listeTypedQuery.getResultStream().findFirst().orElse(null);
    }

    @Override
    public KøSortering hentSorteringForListe(Long listeId) {
        TypedQuery<KøSortering> listeTypedQuery = entityManager
                .createQuery("SELECT l.sortering FROM OppgaveFiltrering l WHERE l.id = :id ", KøSortering.class)
                .setParameter("id", listeId);
        return listeTypedQuery.getResultStream().findFirst().orElse(null);
    }


    @Override
    public void lagre(Reservasjon reservasjon){
        internLagre(reservasjon);
    }

    @Override
    public void lagre(Oppgave oppgave){
        internLagre(oppgave);
    }

    @Override
    public void lagre(TilbakekrevingOppgave egenskaper){
        internLagre(egenskaper);
    }

    @Override
    public void lagre(FiltreringBehandlingType filtreringBehandlingType) {
        internLagre(filtreringBehandlingType);
    }

    @Override
    public void lagre(FiltreringYtelseType filtreringYtelseType) {
        internLagre(filtreringYtelseType);
    }

    @Override
    public void lagre(FiltreringAndreKriterierType filtreringAndreKriterierType) {
        internLagre(filtreringAndreKriterierType);
    }

    @Override
    public Long lagre(OppgaveFiltrering oppgaveFiltrering) {
        internLagre(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    @Override
    public void oppdaterNavn(Long sakslisteId, String navn) {
        entityManager.persist(
                entityManager.find(OppgaveFiltreringOppdaterer.class, sakslisteId)
                    .endreNavn(navn));
        entityManager.flush();
    }

    @Override
    public void slettListe(Long listeId) {
        entityManager.remove(entityManager.find(OppgaveFiltrering.class, listeId));
        entityManager.flush();
    }

    @Override
    public void slettFiltreringBehandlingType(Long sakslisteId, BehandlingType behandlingType) {
        entityManager.createNativeQuery("DELETE FROM FILTRERING_BEHANDLING_TYPE f " +
                "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.behandling_type = :behandlingType")
                .setParameter("oppgaveFiltreringId", sakslisteId)//$NON-NLS-1$ // NOSONAR
                .setParameter("behandlingType", behandlingType.getKode())
                .executeUpdate();
    }

    @Override
    public void slettFiltreringYtelseType(Long sakslisteId, FagsakYtelseType fagsakYtelseType) {
        entityManager.createNativeQuery("DELETE FROM FILTRERING_YTELSE_TYPE f " +
                "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.FAGSAK_YTELSE_TYPE = :fagsakYtelseType")
                .setParameter("oppgaveFiltreringId", sakslisteId)//$NON-NLS-1$ // NOSONAR
                .setParameter("fagsakYtelseType", fagsakYtelseType.getKode())
                .executeUpdate();
    }

    @Override
    public void slettFiltreringAndreKriterierType(Long oppgavefiltreringId, AndreKriterierType andreKriterierType) {
        entityManager.createNativeQuery("DELETE FROM FILTRERING_ANDRE_KRITERIER f " +
                "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.ANDRE_KRITERIER_TYPE = :andreKriterierType")
                .setParameter("oppgaveFiltreringId", oppgavefiltreringId)//$NON-NLS-1$ // NOSONAR
                .setParameter("andreKriterierType", andreKriterierType.getKode())
                .executeUpdate();
    }

    @Override
    public void refresh(Oppgave oppgave) {
        entityManager.refresh(oppgave);
    }

    @Override
    public void refresh(OppgaveFiltrering oppgaveFiltrering) {
        entityManager.refresh(oppgaveFiltrering);
    }

    @Override
    public void refresh(Avdeling avdeling) {
        entityManager.refresh(avdeling);
    }

    @Override
    public void refresh(Saksbehandler saksbehandler) {
        entityManager.refresh(saksbehandler);
    }

    @Override
    public List<Oppgave> sjekkOmOppgaverFortsattErTilgjengelige(List<Long> oppgaveIder) {
        return entityManager.createQuery(SELECT_FRA_OPPGAVE +
                " INNER JOIN avdeling a ON a.avdelingEnhet = o.behandlendeEnhet WHERE " +
                "NOT EXISTS (select r from Reservasjon r where r.oppgave = o and r.reservertTil > :naa) " +
                "AND o.id IN ( :oppgaveId ) " +
                "AND o.aktiv = true", Oppgave.class) //$NON-NLS-1$
                .setParameter("naa", LocalDateTime.now())
                .setParameter("oppgaveId",oppgaveIder)
                .getResultList();

    }

    @Override
    public Oppgave opprettOppgave(Oppgave oppgave) {
        internLagre(oppgave);
        entityManager.refresh(oppgave);
        return oppgave;
    }

    @Override
    public TilbakekrevingOppgave opprettTilbakekrevingEgenskaper(TilbakekrevingOppgave egenskaper) {
        internLagre(egenskaper);
        entityManager.refresh(egenskaper);
        return egenskaper;
    }

    @Override
    public Oppgave gjenåpneOppgaveForBehandling(BehandlingId behandlingId) {
        List<Oppgave> oppgaver = hentOppgaver(behandlingId, Oppgave.class);
        Oppgave sisteOppgave = oppgaver.stream()
                .max(Comparator.comparing(Oppgave::getOpprettetTidspunkt))
                .orElse(null);
        if (sisteOppgave != null) {
            sisteOppgave.gjenåpneOppgave();
            internLagre(sisteOppgave);
            entityManager.refresh(sisteOppgave);
        }
        return sisteOppgave;
    }

    @Override
    public TilbakekrevingOppgave gjenåpneTilbakekrevingOppgave(BehandlingId behandlingId) {
        List<TilbakekrevingOppgave> oppgaver = hentOppgaver(behandlingId, TilbakekrevingOppgave.class);
        var sisteOppgave = oppgaver.stream()
                .max(Comparator.comparing(Oppgave::getOpprettetTidspunkt))
                .orElse(null);
        if (sisteOppgave != null) {
            sisteOppgave.gjenåpneOppgave();
            internLagre(sisteOppgave);
            entityManager.refresh(sisteOppgave);
        }
        return sisteOppgave;
    }

    @Override
    public void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        List<Oppgave> oppgaver = hentOppgaver(behandlingId, Oppgave.class);
        if (oppgaver.isEmpty()) {
            return;
        }
        Oppgave nyesteOppgave = oppgaver.stream()
                .max(Comparator.comparing(Oppgave::getOpprettetTidspunkt))
                .orElse(null);
        frigiEventuellReservasjon(nyesteOppgave.getReservasjon());
        nyesteOppgave.avsluttOppgave();
        internLagre(nyesteOppgave);
        entityManager.refresh(nyesteOppgave);
    }

    private void frigiEventuellReservasjon(Reservasjon reservasjon) {
        if (reservasjon != null && reservasjon.erAktiv()) {
            reservasjon.frigiReservasjon("Oppgave avsluttet");
            lagre(reservasjon);
            lagre(new ReservasjonEventLogg(reservasjon));
        }
    }

    @Override
    public List<Oppgave> hentSisteReserverteOppgaver(String uid) {
        return entityManager.createQuery("SELECT o FROM Oppgave o " +
                "INNER JOIN Reservasjon r ON r.oppgave = o " +
                "WHERE upper(r.reservertAv) = upper( :uid ) ORDER BY coalesce(r.endretTidspunkt, r.opprettetTidspunkt) DESC ", Oppgave.class) //$NON-NLS-1$
                .setParameter("uid", uid).setMaxResults(10).getResultList();
    }

    @Override
    public void lagre(OppgaveEgenskap oppgaveEgenskap) {
        internLagre(oppgaveEgenskap);
        refresh(oppgaveEgenskap.getOppgave());
    }

    @Override
    public void lagre(EventmottakFeillogg eventmottakFeillogg) {
        internLagre(eventmottakFeillogg);
    }

    @Override
    public List<OppgaveEventLogg> hentOppgaveEventer(BehandlingId behandlingId) {
        Objects.requireNonNull(behandlingId, "behandlingId kan ikke være null");
        return entityManager.createQuery("FROM oppgaveEventLogg oel " +
                "where oel.behandlingId = :behandlingId " +
                "order by oel.opprettetTidspunkt desc", OppgaveEventLogg.class)
                .setParameter("behandlingId", behandlingId.toUUID()).getResultList();
    }

    @Override
    public List<OppgaveEgenskap> hentOppgaveEgenskaper(Long oppgaveId) {
        return entityManager.createQuery("FROM OppgaveEgenskap oe " +
                "where oe.oppgaveId = :oppgaveId ORDER BY oe.id desc", OppgaveEgenskap.class)
                .setParameter("oppgaveId", oppgaveId).getResultList();
    }

    @Override
    public void lagre(OppgaveEventLogg oppgaveEventLogg) {
        internLagre(oppgaveEventLogg);
    }

    @Override
    public void lagre(ReservasjonEventLogg reservasjonEventLogg) {
        internLagre(reservasjonEventLogg);
    }

    private <T> List<T> hentOppgaver(BehandlingId behandlingId, Class<T> cls) {
        var select = cls.equals(TilbakekrevingOppgave.class)
                ? SELECT_FRA_TILBAKEKREVING_OPPGAVE
                : SELECT_FRA_OPPGAVE;
        return entityManager.createQuery(select +
                "WHERE o.behandlingId = :behandlingId ", cls)
                .setParameter("behandlingId", behandlingId.toUUID())
                .getResultList();
    }

    @Override
    public void settSortering(Long sakslisteId, String sortering) {
        entityManager.persist(
                entityManager.find(OppgaveFiltreringOppdaterer.class, sakslisteId)
                        .endreSortering(sortering)
                        .endreErDynamiskPeriode(false)
                        .endreFomDato(null)
                        .endreTomDato(null)
                        .endreFraVerdi(null)
                        .endreTilVerdi(null));
        entityManager.flush();
    }

    @Override
    public void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato){
        entityManager.persist(
                entityManager.find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
                        .endreFomDato(fomDato)
                        .endreTomDato(tomDato));
        entityManager.flush();
    }

    @Override
    public void settSorteringNumeriskIntervall(Long oppgaveFiltreringId, Long fra, Long til){
        entityManager.persist(
                entityManager.find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
                        .endreFraVerdi(fra)
                        .endreTilVerdi(til));
        entityManager.flush();
    }

    @Override
    public void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode){
        entityManager.persist(
                entityManager.find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
                        .endreErDynamiskPeriode(erDynamiskPeriode)
                        .endreFomDato(null)
                        .endreTomDato(null)
                        .endreFraVerdi(null)
                        .endreTilVerdi(null));
        entityManager.flush();
    }

    @Override
    public List<Oppgave> hentOppgaverForSynkronisering() {
        return entityManager.createQuery("FROM Oppgave o where o.aktiv = true AND o.system = :system", Oppgave.class)
                .setParameter("system", "FPSAK")
                .getResultList();
    }


    private void internLagre(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }

}
