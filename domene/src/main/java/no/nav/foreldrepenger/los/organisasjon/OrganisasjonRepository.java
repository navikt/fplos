package no.nav.foreldrepenger.los.organisasjon;

import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentEksaktResultat;
import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentUniktResultat;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;


@ApplicationScoped
public class OrganisasjonRepository {

    private EntityManager entityManager;

    @Inject
    public OrganisasjonRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OrganisasjonRepository() {
    }

    public void lagre(Saksbehandler saksbehandler) {
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
}
