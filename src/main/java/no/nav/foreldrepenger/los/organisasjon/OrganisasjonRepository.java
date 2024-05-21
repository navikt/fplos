package no.nav.foreldrepenger.los.organisasjon;

import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentEksaktResultat;
import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentUniktResultat;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.felles.jpa.TomtResultatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    public void refresh(Avdeling avdeling) {
        entityManager.refresh(avdeling);
    }

    private TypedQuery<Saksbehandler> hentSaksbehandlerQuery(String saksbehandlerIdent) {
        return entityManager.createQuery("""
            FROM saksbehandler s
            WHERE upper(s.saksbehandlerIdent) = upper( :ident )
            """, Saksbehandler.class).setParameter("ident", saksbehandlerIdent.toUpperCase());
    }

    public void slettSaksbehandlereUtenKnytninger() {
        int slettedeRader = entityManager.createQuery("""
                delete from saksbehandler s
                where not exists (
                    select ofil
                    from OppgaveFiltrering ofil
                    where s member of ofil.saksbehandlere
                )
                and not exists (
                    select avd
                    from avdeling avd
                    where s member of avd.saksbehandlere
                )
                and not exists (
                   select gruppe
                   from saksbehandlerGruppe gruppe
                   where s member of gruppe.saksbehandlere
                )""")
            .executeUpdate();
        LOG.info("Slettet {} saksbehandlere uten knytninger til køer", slettedeRader);
    }


    public void slettØvrigeEnhetsdata(String avdelingEnhet) {
        var avdeling = hentAvdelingFraEnhet(avdelingEnhet).orElse(null);
        if (avdeling != null && avdeling.getErAktiv()) {
            LOG.info("Enhet er aktiv {}, avslutter.", avdelingEnhet);
            return;
        }
        entityManager.createNativeQuery("DELETE FROM STATISTIKK_KO where OPPGAVE_ID in (select id from oppgave where behandlende_enhet = :enhet)").setParameter("enhet", avdelingEnhet).executeUpdate();;
        entityManager.createNativeQuery("DELETE FROM RESERVASJON_EVENT_LOGG where OPPGAVE_ID in (select id from oppgave where behandlende_enhet = :enhet)").setParameter("enhet", avdelingEnhet).executeUpdate();;
        entityManager.createNativeQuery("DELETE FROM RESERVASJON where OPPGAVE_ID in (select id from oppgave where behandlende_enhet = :enhet)").setParameter("enhet", avdelingEnhet).executeUpdate();;
        entityManager.createNativeQuery("DELETE FROM TILBAKEKREVING_EGENSKAPER where OPPGAVE_ID in (select id from oppgave where behandlende_enhet = :enhet)").setParameter("enhet", avdelingEnhet).executeUpdate();;
        entityManager.createNativeQuery("DELETE FROM OPPGAVE_EGENSKAP where OPPGAVE_ID in (select id from oppgave where behandlende_enhet = :enhet)").setParameter("enhet", avdelingEnhet).executeUpdate();;
        entityManager.createNativeQuery("DELETE FROM OPPGAVE_EVENT_LOGG where behandlende_enhet = :enhet").setParameter("enhet", avdelingEnhet).executeUpdate();;
        entityManager.createNativeQuery("DELETE FROM OPPGAVE where behandlende_enhet = :enhet").setParameter("enhet", avdelingEnhet).executeUpdate();;
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
        var saksbehandler = hentSaksbehandlerHvisEksisterer(saksbehandlerId).filter(sb -> sb.getAvdelinger().contains(gruppe.getAvdeling()));
        saksbehandler.ifPresentOrElse(sb -> {
            gruppe.getSaksbehandlere().add(sb);
            entityManager.persist(gruppe);
        }, () -> {
            throw fantIkkeSaksbehandlerException(saksbehandlerId, avdelingEnhet);
        });
    }

    public void fjernSaksbehandlerFraGruppe(String saksbehandlerIdent, long gruppeId, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        sjekkGruppeEnhetTilknytning(gruppeId, avdelingEnhet, gruppe);
        gruppe.getSaksbehandlere().removeIf(s -> s.getSaksbehandlerIdent().equals(saksbehandlerIdent));
        entityManager.merge(gruppe);
    }

    public void updateSaksbehandlerGruppeNavn(long gruppeId, String gruppeNavn, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        sjekkGruppeEnhetTilknytning(gruppeId, avdelingEnhet, gruppe);
        gruppe.setGruppeNavn(gruppeNavn);
        entityManager.merge(gruppe);
    }

    public void slettSaksbehandlerGruppe(long gruppeId, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        sjekkGruppeEnhetTilknytning(gruppeId, avdelingEnhet, gruppe);
        gruppe.getSaksbehandlere().clear();
        entityManager.merge(gruppe);
        entityManager.remove(gruppe);
        entityManager.flush();
    }

    public void deaktiverAvdeling(String avdelingEnhet) {
        var avdeling = hentAvdelingFraEnhet(avdelingEnhet).orElseThrow(() -> new IllegalStateException("Fant ikke avdeling"));
        avdeling.setErAktiv(false);
        entityManager.persist(avdeling);
    }

    public void slettAvdeling(Avdeling avdeling) {
        var saksbehandlere = avdeling.getSaksbehandlere();
        LOG.info("{} saksbehandlere har tilknytning til avdeling {}, fjerner tilknytning(er).", saksbehandlere.size(), avdeling.getAvdelingEnhet());
        saksbehandlere.forEach(sb -> {
            sb.fjernAvdeling(avdeling);
            entityManager.persist(sb);
        });
        entityManager.remove(avdeling);
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

}
