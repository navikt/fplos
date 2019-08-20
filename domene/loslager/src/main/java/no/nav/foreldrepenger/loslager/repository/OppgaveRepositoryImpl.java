package no.nav.foreldrepenger.loslager.repository;

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
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;

@ApplicationScoped
public class OppgaveRepositoryImpl implements OppgaveRepository {

    private static final String COUNT_FRA_OPPGAVE = "SELECT count(1) from Oppgave o ";
    private static final String SELECT_FRA_OPPGAVE = "SELECT o from Oppgave o ";
    private static final String SORTERING = "ORDER BY ";
    private static final String BEHANDLINGSFRIST = "o.behandlingsfrist";
    private static final String BEHANDLINGOPPRETTET = "o.behandlingOpprettet";
    private static final String FORSTE_STONADSDAG = "o.forsteStonadsdag";
    private static final String OPPGAVEFILTRERING_SORTERING_NAVN = "ORDER BY l.navn";

    private EntityManager entityManager;

    @Inject
    public OppgaveRepositoryImpl(@VLPersistenceUnit EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OppgaveRepositoryImpl(){
    }

    EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public int hentAntallOppgaver(OppgavespørringDto oppgavespørringDto) {
        TypedQuery<Long> oppgaveTypedQuery = lagOppgavespørring(COUNT_FRA_OPPGAVE, Long.class, oppgavespørringDto);
        return oppgaveTypedQuery.getSingleResult().intValue();
    }

    @Override
    public List<Oppgave> hentOppgaver(OppgavespørringDto oppgavespørringDto) {
        TypedQuery<Oppgave> oppgaveTypedQuery = lagOppgavespørring(SELECT_FRA_OPPGAVE, Oppgave.class, oppgavespørringDto);
        return oppgaveTypedQuery.getResultList();
    }

    private <T> TypedQuery<T> lagOppgavespørring(String selection, Class<T> oppgaveClass, OppgavespørringDto oppgavespørringDto) {
        String filtrerBehandlingType = oppgavespørringDto.getBehandlingTyper().isEmpty() ? "": " o.behandlingType in :behtyper AND ";
        String filtrerYtelseType = oppgavespørringDto.getYtelseTyper().isEmpty() ? "": " o.fagsakYtelseType in :fagsakYtelseType AND ";

        StringBuilder filtrerInkluderAndreKriterier = new StringBuilder();
        for(AndreKriterierType andreKriterierType : oppgavespørringDto.getInkluderAndreKriterierTyper()) {
            filtrerInkluderAndreKriterier.append("EXISTS ( SELECT  1 FROM OppgaveEgenskap oe WHERE o = oe.oppgave AND oe.aktiv = true AND oe.andreKriterier = '" + andreKriterierType.getKode() + "' ) AND ");
        }

        StringBuilder filtrerEkskluderAndreKriterier = new StringBuilder();
        for(AndreKriterierType andreKriterierType : oppgavespørringDto.getEkskluderAndreKriterierTyper()) {
            filtrerEkskluderAndreKriterier.append("NOT EXISTS (select 1 from OppgaveEgenskap oen WHERE o = oen.oppgave AND oen.aktiv = true AND oen.andreKriterier = '" + andreKriterierType.getKode() + "') AND ");
        }

        TypedQuery<T> oppgaveTypedQuery = getEntityManager().createQuery(selection + //$NON-NLS-1$ // NOSONAR
                "INNER JOIN avdeling a ON a.avdelingEnhet = o.behandlendeEnhet " +
                "WHERE " +
                filtrerBehandlingType +
                filtrerYtelseType +
                filtrerInkluderAndreKriterier +
                filtrerEkskluderAndreKriterier +
                "NOT EXISTS (select r from Reservasjon r where r.oppgave = o and r.reservertTil > :naa) AND " +
                "NOT EXISTS (select oetilbesl from OppgaveEgenskap oetilbesl " +
                    "where o.id = oetilbesl.oppgaveId AND oetilbesl.aktiv = true AND oetilbesl.andreKriterierType = :tilbeslutter " +
                    "AND upper( oetilbesl.sisteSaksbehandlerForTotrinn) = upper( :uid ) ) " +
                "AND a.id = :enhet " +
                "AND o.aktiv = true " + sortering(oppgavespørringDto), oppgaveClass)
                .setParameter("naa", LocalDateTime.now())
                .setParameter("enhet", oppgavespørringDto.getId())
                .setParameter("tilbeslutter", AndreKriterierType.TIL_BESLUTTER)
                .setParameter("uid", finnBrukernavn());

        if (!oppgavespørringDto.getBehandlingTyper().isEmpty()) {
            oppgaveTypedQuery.setParameter("behtyper", oppgavespørringDto.getBehandlingTyper());
        }
        if (!oppgavespørringDto.getYtelseTyper().isEmpty()) {
            oppgaveTypedQuery.setParameter("fagsakYtelseType", oppgavespørringDto.getYtelseTyper());
        }
        if (oppgavespørringDto.getFiltrerFomDager() != null) {
            oppgaveTypedQuery.setParameter("filterFomDager", KøSortering.FORSTE_STONADSDAG.equals(oppgavespørringDto.getSortering()) ? LocalDate.now().plusDays(oppgavespørringDto.getFiltrerFomDager()) : LocalDateTime.now().plusDays(oppgavespørringDto.getFiltrerFomDager()).with(LocalTime.MIN));
        }
        if (oppgavespørringDto.getFiltrerTomDager() != null) {
            oppgaveTypedQuery.setParameter("filterTomDager", KøSortering.FORSTE_STONADSDAG.equals(oppgavespørringDto.getSortering()) ? LocalDate.now().plusDays(oppgavespørringDto.getFiltrerTomDager()) : LocalDateTime.now().plusDays(oppgavespørringDto.getFiltrerTomDager()).with(LocalTime.MAX));
        }
        if (oppgavespørringDto.getFiltrerFomDato() != null) {
            oppgaveTypedQuery.setParameter("filterFomDato", KøSortering.FORSTE_STONADSDAG.equals(oppgavespørringDto.getSortering()) ? oppgavespørringDto.getFiltrerFomDato() : oppgavespørringDto.getFiltrerFomDato().atTime(LocalTime.MIN));
        }
        if (oppgavespørringDto.getFiltrerTomDato() != null) {
            oppgaveTypedQuery.setParameter("filterTomDato", KøSortering.FORSTE_STONADSDAG.equals(oppgavespørringDto.getSortering()) ? oppgavespørringDto.getFiltrerTomDato() : oppgavespørringDto.getFiltrerTomDato().atTime(LocalTime.MAX));
        }

        return oppgaveTypedQuery;
    }

    private String sortering(OppgavespørringDto oppgavespørringDto) {
        KøSortering sortering = oppgavespørringDto.getSortering();
        if (KøSortering.BEHANDLINGSFRIST.equals(sortering)) {
            return oppgavespørringDto.isErDynamiskPeriode()
                    ? filtrerDynamisk(BEHANDLINGSFRIST, oppgavespørringDto.getFiltrerFomDager(), oppgavespørringDto.getFiltrerTomDager())
                    : filtrerStatisk(BEHANDLINGSFRIST, oppgavespørringDto.getFiltrerFomDato(), oppgavespørringDto.getFiltrerTomDato());
        } else if (KøSortering.OPPRETT_BEHANDLING.equals(sortering)) {
            return oppgavespørringDto.isErDynamiskPeriode()
                    ? filtrerDynamisk(BEHANDLINGOPPRETTET, oppgavespørringDto.getFiltrerFomDager(), oppgavespørringDto.getFiltrerTomDager())
                    : filtrerStatisk(BEHANDLINGOPPRETTET, oppgavespørringDto.getFiltrerFomDato(), oppgavespørringDto.getFiltrerTomDato());
        } else if (KøSortering.FORSTE_STONADSDAG.equals(sortering)) {
            return oppgavespørringDto.isErDynamiskPeriode()
                    ? filtrerDynamisk(FORSTE_STONADSDAG, oppgavespørringDto.getFiltrerFomDager(), oppgavespørringDto.getFiltrerTomDager())
                    : filtrerStatisk(FORSTE_STONADSDAG, oppgavespørringDto.getFiltrerFomDato(), oppgavespørringDto.getFiltrerTomDato());
        } else if (KøSortering.UDEFINERT.equals(sortering)) {
            return SORTERING + BEHANDLINGOPPRETTET;
        } else {
            return SORTERING + BEHANDLINGOPPRETTET;
        }
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
    public List<Reservasjon> hentReserverteOppgaver(String uid){
        TypedQuery<Reservasjon> oppgaveTypedQuery = getEntityManager().createQuery("Select r from Reservasjon r " +
                "INNER JOIN Oppgave o ON r.oppgave = o " +
                "WHERE r.reservertTil > :naa AND upper(r.reservertAv) = upper( :uid ) AND o.aktiv = true", Reservasjon.class) //$NON-NLS-1$
                .setParameter("naa", LocalDateTime.now())
                .setParameter("uid", uid);
        return oppgaveTypedQuery.getResultList();
    }

    @Override
    public List<Oppgave> hentOppgaverForSaksnummer(Long fagsakSaksnummer) {
        return getEntityManager().createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.fagsakSaksnummer = :fagsakSaksnummer " +
                "ORDER BY o.id desc ", Oppgave.class)
                .setParameter("fagsakSaksnummer", fagsakSaksnummer)
                .getResultList();
    }

    @Override
    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe) {
        return getEntityManager().createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.fagsakSaksnummer in :fagsakSaksnummerListe " +
                "AND o.aktiv = true " +
                "ORDER BY o.fagsakSaksnummer desc ", Oppgave.class)
                .setParameter("fagsakSaksnummerListe", fagsakSaksnummerListe)
                .getResultList();
    }

    public Reservasjon hentReservasjon(Long oppgaveId){
        TypedQuery<Reservasjon> oppgaveTypedQuery =  getEntityManager().createQuery("from Reservasjon r WHERE r.oppgave.id = :id ", Reservasjon.class)
            .setParameter("id", oppgaveId);//$NON-NLS-1$
        List<Reservasjon> resultList = oppgaveTypedQuery.getResultList();
        if (resultList.isEmpty()){
            return new Reservasjon(getEntityManager().find(Oppgave.class, oppgaveId));
        }
        return oppgaveTypedQuery.getResultList().get(0);
    }

    @Override
    public void reserverOppgaveFraTidligereReservasjon(Long oppgaveId, Reservasjon tidligereReservasjon){
        Reservasjon reservasjon = hentReservasjon(oppgaveId);
        reservasjon.reserverOppgaveFraTidligereReservasjon(tidligereReservasjon.getReservertTil(),
                tidligereReservasjon.getReservertAv(),
                reservasjon.getFlyttetAv(),
                reservasjon.getFlyttetTidspunkt(),
                reservasjon.getBegrunnelse());
        lagre(reservasjon);
        refresh(reservasjon.getOppgave());
        lagre(new ReservasjonEventLogg(oppgaveId, reservasjon));
    }

    @Override
    public List<OppgaveFiltrering> hentAlleLister(Long avdelingsId) {
        TypedQuery<OppgaveFiltrering> listeTypedQuery = getEntityManager()
                .createQuery("FROM OppgaveFiltrering l WHERE l.avdeling.id = :id " +
                        OPPGAVEFILTRERING_SORTERING_NAVN, OppgaveFiltrering.class)
                .setParameter("id", avdelingsId);//$NON-NLS-1$
        return listeTypedQuery.getResultList();
    }

    @Override
    public OppgaveFiltrering hentListe(Long listeId) {
        TypedQuery<OppgaveFiltrering> listeTypedQuery = getEntityManager()
                .createQuery("FROM OppgaveFiltrering l WHERE l.id = :id " +
                        OPPGAVEFILTRERING_SORTERING_NAVN, OppgaveFiltrering.class)
                .setParameter("id", listeId);
        return listeTypedQuery.getResultList().get(0);
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
        getEntityManager().persist(
                getEntityManager().find(OppgaveFiltreringOppdaterer.class, sakslisteId)
                    .endreNavn(navn));
        getEntityManager().flush();
    }

    @Override
    public void slettListe(Long listeId) {
        getEntityManager().remove(getEntityManager().find(OppgaveFiltrering.class, listeId));
        getEntityManager().flush();
    }

    @Override
    public void slettFiltreringBehandlingType(Long sakslisteId, BehandlingType behandlingType) {
        getEntityManager().createNativeQuery("DELETE FROM FILTRERING_BEHANDLING_TYPE f " +
                "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.behandling_type = :behandlingType")
                .setParameter("oppgaveFiltreringId", sakslisteId)//$NON-NLS-1$ // NOSONAR
                .setParameter("behandlingType", behandlingType.getKode())
                .executeUpdate();
    }

    @Override
    public void slettFiltreringYtelseType(Long sakslisteId, FagsakYtelseType fagsakYtelseType) {
        getEntityManager().createNativeQuery("DELETE FROM FILTRERING_YTELSE_TYPE f " +
                "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.FAGSAK_YTELSE_TYPE = :fagsakYtelseType")
                .setParameter("oppgaveFiltreringId", sakslisteId)//$NON-NLS-1$ // NOSONAR
                .setParameter("fagsakYtelseType", fagsakYtelseType.getKode())
                .executeUpdate();
    }

    @Override
    public void slettFiltreringAndreKriterierType(Long oppgavefiltreringId, AndreKriterierType andreKriterierType) {
        getEntityManager().createNativeQuery("DELETE FROM FILTRERING_ANDRE_KRITERIER f " +
                "WHERE f.OPPGAVE_FILTRERING_ID = :oppgaveFiltreringId and f.ANDRE_KRITERIER_TYPE = :andreKriterierType")
                .setParameter("oppgaveFiltreringId", oppgavefiltreringId)//$NON-NLS-1$ // NOSONAR
                .setParameter("andreKriterierType", andreKriterierType.getKode())
                .executeUpdate();
    }

    @Override
    public void refresh(Oppgave oppgave) {
        getEntityManager().refresh(oppgave);
    }

    @Override
    public void refresh(OppgaveFiltrering oppgaveFiltrering) {
        getEntityManager().refresh(oppgaveFiltrering);
    }

    @Override
    public void refresh(Avdeling avdeling) {
        getEntityManager().refresh(avdeling);
    }

    @Override
    public void refresh(Saksbehandler saksbehandler) {
        getEntityManager().refresh(saksbehandler);
    }

    @Override
    public List<Oppgave> sjekkOmOppgaverFortsattErTilgjengelige(List<Long> oppgaveIder) {
        return getEntityManager().createQuery(SELECT_FRA_OPPGAVE +
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
    public Oppgave gjenåpneOppgave(Long behandlingId) {
        Oppgave oppgave = hentOppgaver(behandlingId).get(0);
        oppgave.gjenåpneOppgave();
        internLagre(oppgave);
        entityManager.refresh(oppgave);
        return oppgave;
    }

    @Override
    public void avsluttOppgave(Long behandlingId) {
        List<Oppgave> oppgaver = hentOppgaver(behandlingId);
        if(oppgaver.isEmpty())return;
        Oppgave oppgave = oppgaver.get(0);
        Reservasjon reservasjon = oppgave.getReservasjon();
        if(reservasjon != null && reservasjon.getReservertTil().isAfter(LocalDateTime.now())) {
            reservasjon.frigiOppgave(reservasjon.getReservertAv(), "Oppgave avsluttet");
            lagre(reservasjon);
            lagre(new ReservasjonEventLogg(oppgave.getId(), reservasjon));
        }
        oppgave.avsluttOppgave();
        internLagre(oppgave);
        entityManager.refresh(oppgave);
    }

    @Override
    public List<Oppgave> hentSisteReserverteOppgaver(String uid) {
         return getEntityManager().createQuery("SELECT o FROM Oppgave o " +
                "INNER JOIN Reservasjon r ON r.oppgave = o " +
                "WHERE upper(r.reservertAv) = upper( :uid ) ORDER BY coalesce(r.endretTidspunkt,r.opprettetTidspunkt) DESC ", Oppgave.class) //$NON-NLS-1$
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
    public List<OppgaveEventLogg> hentEventer(Long behandlingId) {
        return getEntityManager().createQuery("FROM oppgaveEventLogg oel " +
                "where oel.behandlingId = :behandlingId ORDER BY oel.id desc", OppgaveEventLogg.class)
                .setParameter("behandlingId", behandlingId).getResultList();
    }

    @Override
    public List<OppgaveEgenskap> hentOppgaveEgenskaper(Long oppgaveId) {
        return getEntityManager().createQuery("FROM OppgaveEgenskap oe " +
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

    private List<Oppgave> hentOppgaver(Long behandlingId) {
        return getEntityManager().createQuery(SELECT_FRA_OPPGAVE +
                "WHERE o.behandlingId = :behandlingId " +
                "ORDER BY o.id desc ", Oppgave.class)
                .setParameter("behandlingId",behandlingId)
                .getResultList();
    }

    @Override
    public void settSortering(Long sakslisteId, String sortering) {
        getEntityManager().persist(
                getEntityManager().find(OppgaveFiltreringOppdaterer.class, sakslisteId)
                        .endreSortering(sortering)
                        .endreErDynamiskPeriode(false)
                        .endreFomDato(null)
                        .endreTomDato(null)
                        .endreFomDager(null)
                        .endreTomDager(null));
        entityManager.flush();
    }

    @Override
    public void settSorteringTidsintervallDato(Long oppgaveFiltreringId, LocalDate fomDato, LocalDate tomDato){
        getEntityManager().persist(
                getEntityManager().find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
                        .endreFomDato(fomDato)
                        .endreTomDato(tomDato));
        entityManager.flush();
    }

    @Override
    public void settSorteringTidsintervallDager(Long oppgaveFiltreringId, Long fomDager, Long tomDager){
        getEntityManager().persist(
                getEntityManager().find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
                        .endreFomDager(fomDager)
                        .endreTomDager(tomDager));
        entityManager.flush();
    }

    @Override
    public void settSorteringTidsintervallValg(Long oppgaveFiltreringId, boolean erDynamiskPeriode){
        getEntityManager().persist(
                getEntityManager().find(OppgaveFiltreringOppdaterer.class, oppgaveFiltreringId)
                        .endreErDynamiskPeriode(erDynamiskPeriode)
                        .endreFomDato(null)
                        .endreTomDato(null)
                        .endreFomDager(null)
                        .endreTomDager(null));
        entityManager.flush();
    }


    private void internLagre(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }
}
