package no.nav.foreldrepenger.los.organisasjon;

import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentEksaktResultat;
import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentUniktResultat;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.exception.FunksjonellException;


@ApplicationScoped
public class OrganisasjonRepository {

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

    public Optional<Avdeling> hentAvdelingFraEnhet(String avdelingEnhet) {
        var query = entityManager.createQuery("FROM avdeling a WHERE a.avdelingEnhet = :avdelingEnhet", Avdeling.class)
            .setParameter("avdelingEnhet", avdelingEnhet);
        return hentUniktResultat(query);
    }

    public List<Avdeling> hentAvdelinger() {
        var listeTypedQuery = entityManager.createQuery("FROM avdeling ", Avdeling.class);
        return listeTypedQuery.getResultList();
    }

    public List<SaksbehandlerGruppe> hentSaksbehandlerGrupper(String avdelingEnhet) {
        return entityManager.createQuery("FROM saksbehandlerGruppe g where g.avdeling.avdelingEnhet = :avdelingEnhet", SaksbehandlerGruppe.class)
            .setParameter("avdelingEnhet", avdelingEnhet)
            .getResultList();
    }

    public void leggSaksbehandlerTilGruppe(String saksbehandlerId, int gruppeId, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        if (gruppe == null || !gruppe.getAvdeling().getAvdelingEnhet().equals(avdelingEnhet)) {
            throw new FunksjonellException("FP-164687", String.format("Fant ikke gruppe %s for avdeling %s", gruppeId, avdelingEnhet),
                "Kontroller logisk sammenheng mellom gruppe og enhet");
        }
        var saksbehandler = hentSaksbehandlerHvisEksisterer(saksbehandlerId).filter(sb -> sb.getAvdelinger().contains(gruppe.getAvdeling()));
        saksbehandler.ifPresentOrElse(sb -> {
            gruppe.getSaksbehandlere().add(sb);
            entityManager.persist(gruppe);
        }, () -> {
            throw new FunksjonellException("FP-164687",
                String.format("Fant ikke saksbehandler %s tilknyttet avdeling %s", saksbehandlerId, avdelingEnhet),
                "Kontroller logisk sammenheng mellom saksbehandler, gruppe og enhet");
        });
    }

    public void fjernSaksbehandlerFraGruppe(String saksbehandlerIdent, int gruppeId) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        if (gruppe == null) {
            throw new IllegalArgumentException("Fant ikke gruppe");
        }
        gruppe.getSaksbehandlere().removeIf(s -> s.getSaksbehandlerIdent().equals(saksbehandlerIdent));
        entityManager.persist(gruppe);
    }

    public void updateSaksbehandlerGruppeNavn(long gruppeId, String gruppeNavn) {
        entityManager.createQuery("UPDATE saksbehandlerGruppe g SET g.gruppeNavn = :gruppeNavn WHERE g.id = :gruppeId")
            .setParameter("gruppeNavn", gruppeNavn)
            .setParameter("gruppeId", gruppeId)
            .executeUpdate();
    }

    public void slettSaksbehandlerGruppe(long gruppeId, String avdelingEnhet) {
        var gruppe = entityManager.find(SaksbehandlerGruppe.class, gruppeId);
        if (gruppe == null || !gruppe.getAvdeling().getAvdelingEnhet().equals(avdelingEnhet)) {
            throw new FunksjonellException("FP-164687", String.format("Fant ikke gruppe %s for avdeling %s", gruppeId, avdelingEnhet),
                "Kontroller logisk sammenheng mellom gruppe og enhet");
        }
        gruppe.getSaksbehandlere().clear();
        entityManager.persist(gruppe);
        entityManager.createQuery("DELETE FROM saksbehandlerGruppe g WHERE g.id = :gruppeId")
            .setParameter("gruppeId", gruppeId)
            .executeUpdate();
    }
}
