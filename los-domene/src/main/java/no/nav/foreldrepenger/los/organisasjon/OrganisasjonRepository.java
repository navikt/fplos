package no.nav.foreldrepenger.los.organisasjon;

import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentEksaktResultat;
import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentUniktResultat;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


@ApplicationScoped
public class OrganisasjonRepository {

    private EntityManager entityManager;

    @Inject
    public OrganisasjonRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OrganisasjonRepository(){
    }

    private void internLagre(Object skaLagres) {
        entityManager.persist(skaLagres);
        entityManager.flush();
    }

    public void lagre(Saksbehandler saksbehandler) {
        internLagre(saksbehandler);
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
        return entityManager.createQuery("FROM saksbehandler s WHERE upper(s.saksbehandlerIdent) = upper( :saksbehandlerIdent )", Saksbehandler.class)
                    .setParameter("saksbehandlerIdent", saksbehandlerIdent.toUpperCase());
    }

    public Optional<Avdeling> hentAvdelingFraEnhet(String avdelingEnhet){
        var query = entityManager.createQuery("FROM avdeling a WHERE a.avdelingEnhet = :avdelingEnhet", Avdeling.class)
                .setParameter("avdelingEnhet", avdelingEnhet);
        return hentUniktResultat(query);
    }

    public List<Avdeling> hentAvdelinger() {
        var listeTypedQuery = entityManager.createQuery("FROM avdeling ", Avdeling.class);
        return listeTypedQuery.getResultList();
    }
}
