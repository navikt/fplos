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
public class OrganisasjonRepositoryImpl implements OrganisasjonRepository {

    private EntityManager entityManager;

    @Inject
    public OrganisasjonRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    OrganisasjonRepositoryImpl(){
    }

    private void internLagre(Object skaLagres) {
        entityManager.persist(skaLagres);
        entityManager.flush();
    }

    @Override
    public void lagre(Saksbehandler saksbehandler) {
        internLagre(saksbehandler);
    }

    @Override
    public Saksbehandler hentSaksbehandler(String saksbehandlerIdent) {
        return hentEksaktResultat(hentSaksbehandlerQuery(saksbehandlerIdent));
    }

    @Override
    public Optional<Saksbehandler> hentSaksbehandlerHvisEksisterer(String saksbehandlerIdent) {
        return hentUniktResultat(hentSaksbehandlerQuery(saksbehandlerIdent));
    }

    @Override
    public void refresh(Avdeling avdeling) {
        entityManager.refresh(avdeling);
    }

    private TypedQuery<Saksbehandler> hentSaksbehandlerQuery(String saksbehandlerIdent) {
        return entityManager.createQuery("FROM saksbehandler s WHERE upper(s.saksbehandlerIdent) = upper( :saksbehandlerIdent )", Saksbehandler.class)
                    .setParameter("saksbehandlerIdent", saksbehandlerIdent.toUpperCase());
    }

    @Override
    public Optional<Avdeling> hentAvdelingFraEnhet(String avdelingEnhet){
        TypedQuery<Avdeling> query = entityManager.createQuery("FROM avdeling a WHERE a.avdelingEnhet = :avdelingEnhet", Avdeling.class)
                .setParameter("avdelingEnhet", avdelingEnhet);
        return hentUniktResultat(query);
    }

    @Override
    public List<Avdeling> hentAvdelinger() {
        TypedQuery<Avdeling> listeTypedQuery = entityManager.createQuery("FROM avdeling ", Avdeling.class);
        return listeTypedQuery.getResultList();
    }
}