package no.nav.foreldrepenger.loslager.repository;

import no.nav.fplos.kodeverk.KodeverkRepository;
import no.nav.fplos.kodeverk.KodeverkRepositoryImpl;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Objects;

/** Provider for å enklere å kunne hente ut ulike repository uten for mange injection points. */
@ApplicationScoped
public class OppgaveRepositoryProviderImpl implements OppgaveRepositoryProvider {
    private OppgaveRepository oppgaveRepository;
    private OrganisasjonRepository organisasjonRepository;
    private StatistikkRepository statistikkRepository;
    private AdminRepositoryImpl adminRepository;

    OppgaveRepositoryProviderImpl() {
        // for CDI proxy
    }

    @Inject
    public OppgaveRepositoryProviderImpl(@VLPersistenceUnit EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        this.organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        this.statistikkRepository = new StatiskikkRepositoryImpl(entityManager);
        this.adminRepository = new AdminRepositoryImpl(entityManager);
    }


    @Override
    public OppgaveRepository getOppgaveRepository() {
        return oppgaveRepository;
    }

    @Override
    public OrganisasjonRepository getOrganisasjonRepository() {
        return organisasjonRepository;
    }

    @Override
    public StatistikkRepository getStatisikkRepository() {
        return statistikkRepository;
    }

    @Override
    public AdminRepository getAdminRepository() {
        return adminRepository;
    }
}
