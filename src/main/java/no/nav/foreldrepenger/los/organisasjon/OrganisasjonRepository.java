package no.nav.foreldrepenger.los.organisasjon;

import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentEksaktResultat;
import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentUniktResultat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.felles.jpa.TomtResultatException;


@ApplicationScoped
public class OrganisasjonRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonRepository.class);

    private EntityManager entityManager;

    @Inject
    public OrganisasjonRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OrganisasjonRepository() {
    }

    public <T extends BaseEntitet> void persistFlush(T saksbehandler) {
        entityManager.persist(saksbehandler);
        entityManager.flush();
    }

    public Saksbehandler hentSaksbehandler(String saksbehandlerIdent) {
        return hentEksaktResultat(hentSaksbehandlerQuery(saksbehandlerIdent));
    }

    public Optional<Saksbehandler> hentSaksbehandlerHvisEksisterer(String saksbehandlerIdent) {
        return hentUniktResultat(hentSaksbehandlerQuery(saksbehandlerIdent));
    }

    public List<Saksbehandler> hentAlleSaksbehandlere() {
        return entityManager.createQuery("from saksbehandler", Saksbehandler.class).getResultList();
    }

    public int fjernSaksbehandlereSomHarSluttet() {
        var slettes = entityManager.createQuery("from saksbehandler where navn is null", Saksbehandler.class).getResultList();
        var antall = slettes.size();
        var identer = slettes.stream().map(Saksbehandler::getSaksbehandlerIdent).collect(Collectors.toSet());
        var sbIds = slettes.stream().map(Saksbehandler::getId).collect(Collectors.toSet());
        entityManager.createQuery("DELETE from GruppeTilknytningRelasjon where saksbehandler.id in :sb").setParameter("sb", sbIds).executeUpdate();
        entityManager.createQuery("DELETE from FiltreringSaksbehandlerRelasjon where saksbehandler.id in :sb").setParameter("sb", sbIds).executeUpdate();
        entityManager.createQuery("DELETE from AvdelingSaksbehandlerRelasjon where saksbehandler.id in :sb").setParameter("sb", sbIds).executeUpdate();
        entityManager.createQuery("DELETE from saksbehandler where id in :sb").setParameter("sb", sbIds).executeUpdate();
        entityManager.flush();
        LOG.info("Oppdater saksbehandler: Fjernet {} saksbehandlere som ikke lenger finnes {}", antall, identer);
        return antall;
    }

    public void refresh(Avdeling avdeling) {
        entityManager.refresh(avdeling);
    }

    private TypedQuery<Saksbehandler> hentSaksbehandlerQuery(String saksbehandlerIdent) {
        return entityManager.createQuery("""
            FROM saksbehandler s
            WHERE upper(s.saksbehandlerIdent) = upper( :ident )
            """, Saksbehandler.class).setParameter("ident", saksbehandlerIdent.trim().toUpperCase());
    }

    public void slettSaksbehandlereUtenKnytninger() {
        int slettedeRader = entityManager.createQuery("""
                delete from saksbehandler s
                where not exists (
                    select ofil
                    from FiltreringSaksbehandlerRelasjon ofil
                    where s = ofil.saksbehandler
                )
                and not exists (
                    select avd
                    from AvdelingSaksbehandlerRelasjon avd
                    where s = avd.saksbehandler
                )
                and not exists (
                   select gruppe
                   from GruppeTilknytningRelasjon gruppe
                   where s = gruppe.saksbehandler
                )""")
            .executeUpdate();
        LOG.info("Slettet {} saksbehandlere uten knytninger til køer", slettedeRader);
    }

    public void slettLøseGruppeKnytninger() {
        int slettedeRader = entityManager.createQuery("""
                delete from GruppeTilknytningRelasjon gt
                where not exists (select gt.saksbehandler.id
                                  from AvdelingSaksbehandlerRelasjon avd join saksbehandlerGruppe sg on sg.avdeling.id = avd.avdeling.id
                                  where avd.saksbehandler.id = gt.saksbehandler.id)
                """)
            .executeUpdate();
        LOG.info("Slettet {} løse gruppe-knytninger", slettedeRader);
    }

    public void slettØvrigeEnhetsdata(String avdelingEnhet) {
        var avdeling = hentAvdelingFraEnhet(avdelingEnhet).orElse(null);
        if (avdeling != null && avdeling.getErAktiv()) {
            LOG.info("Enhet er aktiv {}, avslutter.", avdelingEnhet);
            return;
        }
        entityManager.createQuery("DELETE FROM Reservasjon where oppgave.id in (select id from Oppgave where behandlendeEnhet = :enhetr)").setParameter("enhetr", avdelingEnhet).executeUpdate();
        entityManager.createQuery("DELETE FROM OppgaveEgenskap where oppgave.id in (select id from Oppgave where behandlendeEnhet = :enhete)").setParameter("enhete", avdelingEnhet).executeUpdate();
        entityManager.createQuery("DELETE FROM Oppgave where behandlendeEnhet = :enhet").setParameter("enhet", avdelingEnhet).executeUpdate();
        entityManager.flush();
    }

    public Optional<Avdeling> hentAvdelingFraEnhet(String avdelingEnhet) {
        var query = entityManager.createQuery("FROM avdeling a WHERE a.avdelingEnhet = :avdelingEnhet", Avdeling.class)
            .setParameter("avdelingEnhet", avdelingEnhet);
        return hentUniktResultat(query);
    }

    public List<Avdeling> hentAktiveAvdelinger() {
        var query = entityManager.createQuery("FROM avdeling WHERE erAktiv = :erAktiv", Avdeling.class);
        query.setParameter("erAktiv", true);
        return query.getResultList();
    }

    public List<SaksbehandlerGruppe> hentSaksbehandlerGrupper(String avdelingEnhet) {
        return entityManager.createQuery("FROM saksbehandlerGruppe g where g.avdeling.avdelingEnhet = :avdelingEnhet", SaksbehandlerGruppe.class)
            .setParameter("avdelingEnhet", avdelingEnhet)
            .getResultList();
    }

    public void leggSaksbehandlerTilGruppe(String saksbehandlerId, long gruppeId, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        sjekkGruppeEnhetTilknytning(gruppeId, avdelingEnhet, gruppe);
        var saksbehandler = hentSaksbehandlerHvisEksisterer(saksbehandlerId)
            .filter(sb -> avdelingerForSaksbehandler(sb).contains(gruppe.getAvdeling()));
        saksbehandler.ifPresentOrElse(sb -> tilknyttGruppeSaksbehandler(gruppe, sb), () -> {
            throw fantIkkeSaksbehandlerException(saksbehandlerId, avdelingEnhet);
        });
    }

    public void fjernSaksbehandlerFraGruppe(String saksbehandlerIdent, long gruppeId, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        sjekkGruppeEnhetTilknytning(gruppeId, avdelingEnhet, gruppe);
        hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).ifPresent(s -> fraknyttGruppeSaksbehandler(gruppe, s));
    }

    public void updateSaksbehandlerGruppeNavn(long gruppeId, String gruppeNavn, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        sjekkGruppeEnhetTilknytning(gruppeId, avdelingEnhet, gruppe);
        gruppe.setGruppeNavn(gruppeNavn);
    }

    public void slettSaksbehandlerGruppe(long gruppeId, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        sjekkGruppeEnhetTilknytning(gruppeId, avdelingEnhet, gruppe);
        saksbehandlereForGruppe(gruppe)
            .forEach(s -> fraknyttGruppeSaksbehandler(gruppe, s));
        entityManager.remove(gruppe);
    }

    public void opprettEllerReaktiverAvdeling(String avdelingEnhet, String avdelingNavn) {
        var avdeling = hentAvdelingFraEnhet(avdelingEnhet);
        if (avdeling.isPresent()) {
            avdeling.get().setErAktiv(true);
            entityManager.persist(avdeling.get());
        } else {
            var nyAvdeling = new Avdeling(avdelingEnhet, avdelingNavn, false);
            entityManager.persist(nyAvdeling);
        }
    }

    public void deaktiverAvdeling(String avdelingEnhet) {
        var avdeling = hentAvdelingFraEnhet(avdelingEnhet).orElseThrow(() -> new IllegalStateException("Fant ikke avdeling"));
        avdeling.setErAktiv(false);
        entityManager.persist(avdeling);
    }

    public void slettAvdeling(Avdeling avdeling) {
        var knyttet = fraknyttAlleSaksbehandlereFraAvdeling(avdeling);
        LOG.info("{} saksbehandlere har tilknytning til avdeling {}, fjerner tilknytning(er).", knyttet, avdeling.getAvdelingEnhet());
        entityManager.remove(avdeling);
        entityManager.flush();
    }

    private static void sjekkGruppeEnhetTilknytning(long gruppeId, String avdelingEnhet, SaksbehandlerGruppe gruppe) {
        if (gruppe == null || !gruppe.getAvdeling().getAvdelingEnhet().equals(avdelingEnhet)) {
            throw fantIkkeGruppeException(gruppeId, avdelingEnhet);
        }
    }

    private static TomtResultatException fantIkkeGruppeException(long gruppeId, String avdelingEnhet) {
        return new TomtResultatException("FP-164688", String.format("Fant ikke gruppe %s for avdeling %s", gruppeId, avdelingEnhet));
    }

    private static TomtResultatException fantIkkeSaksbehandlerException(String saksbehandlerIdent, String avdelingEnhet) {
        return new TomtResultatException("FP-164689",
            String.format("Fant ikke saksbehandler %s tilknyttet avdeling %s", saksbehandlerIdent, avdelingEnhet));
    }

    public void tilknyttAvdelingSaksbehandler(Avdeling avdeling, Saksbehandler saksbehandler) {
        var nøkkel = new AvdelingSaksbehandlerNøkkel(saksbehandler, avdeling);
        if (entityManager.find(AvdelingSaksbehandlerRelasjon.class, nøkkel) == null) {
            var knytning = new AvdelingSaksbehandlerRelasjon(nøkkel);
            entityManager.persist(knytning);
        }
    }

    public void fraknyttAvdelingSaksbehandler(Avdeling avdeling, Saksbehandler saksbehandler) {
        entityManager.createQuery("DELETE from AvdelingSaksbehandlerRelasjon where saksbehandler = :saksbehandler and avdeling = :avdeling")
            .setParameter("saksbehandler", saksbehandler)
            .setParameter("avdeling", avdeling)
            .executeUpdate();
    }

    public int fraknyttAlleSaksbehandlereFraAvdeling(Avdeling avdeling) {
        var antall = entityManager.createQuery("DELETE from AvdelingSaksbehandlerRelasjon where avdeling = :avdeling")
            .setParameter("avdeling", avdeling)
            .executeUpdate();
        return antall;
    }

    public List<Avdeling> avdelingerForSaksbehandler(Saksbehandler saksbehandler) {
        return entityManager.createQuery("select avdeling from AvdelingSaksbehandlerRelasjon where saksbehandler = :saksbehandler", Avdeling.class)
            .setParameter("saksbehandler", saksbehandler)
            .getResultList();
    }

    public List<Saksbehandler> saksbehandlereForAvdeling(Avdeling avdeling) {
        return entityManager.createQuery("select saksbehandler from AvdelingSaksbehandlerRelasjon where avdeling = :avdeling", Saksbehandler.class)
            .setParameter("avdeling", avdeling)
            .getResultList();
    }

    public void tilknyttGruppeSaksbehandler(SaksbehandlerGruppe gruppe, Saksbehandler saksbehandler) {
        var nøkkel = new GruppeTilknytningNøkkel(saksbehandler, gruppe);
        if (entityManager.find(GruppeTilknytningRelasjon.class, nøkkel) == null) {
            var knytning = new GruppeTilknytningRelasjon(nøkkel);
            entityManager.persist(knytning);
        }
    }

    public void fraknyttGruppeSaksbehandler(SaksbehandlerGruppe gruppe, Saksbehandler saksbehandler) {
        entityManager.createQuery("DELETE from GruppeTilknytningRelasjon where saksbehandler = :saksbehandler and gruppe = :gruppe")
            .setParameter("saksbehandler", saksbehandler)
            .setParameter("gruppe", gruppe)
            .executeUpdate();
    }

    public List<SaksbehandlerGruppe> grupperForSaksbehandler(Saksbehandler saksbehandler) {
        return entityManager.createQuery("select gruppe from GruppeTilknytningRelasjon where saksbehandler = :saksbehandler", SaksbehandlerGruppe.class)
            .setParameter("saksbehandler", saksbehandler)
            .getResultList();
    }

    public List<Saksbehandler> saksbehandlereForGruppe(SaksbehandlerGruppe gruppe) {
        return entityManager.createQuery("select saksbehandler from GruppeTilknytningRelasjon where gruppe = :gruppe", Saksbehandler.class)
            .setParameter("gruppe", gruppe)
            .getResultList();
    }


}
