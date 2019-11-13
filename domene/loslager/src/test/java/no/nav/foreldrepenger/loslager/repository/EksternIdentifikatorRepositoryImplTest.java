package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.EksternIdentifikator;
import no.nav.vedtak.felles.testutilities.db.Repository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EksternIdentifikatorRepositoryImplTest {
    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final Repository repository = repoRule.getRepository();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepositoryProvider repositoryProvider = new OppgaveRepositoryProviderImpl(entityManager);
    private final EksternIdentifikatorRepository eksternIdentifikatorRepository = new EksternIdentifikatorRepositoryImpl(entityManager);

    @Before
    public void setup(){
        entityManager.flush();
    }

    @Test
    public void testFinnIdentifikator(){
        leggInnEttSettMedEksterneIdentifikatorer();
        Optional<EksternIdentifikator> eksternId = eksternIdentifikatorRepository.finnIdentifikator("FPSAK", "2");
        assertTrue(eksternId.isPresent());
    }

    @Test
    public void testFinnEllerOpprettEksternId(){
        leggInnEttSettMedEksterneIdentifikatorer();
        EksternIdentifikator ekststerendeEksternId = eksternIdentifikatorRepository.finnEllerOpprettEksternId("FPSAK", "2");
        assertNotNull(ekststerendeEksternId);

        EksternIdentifikator nyEksternId = eksternIdentifikatorRepository.finnEllerOpprettEksternId("FPSAK", "6");
        assertNotNull(nyEksternId);
    }

    private void leggInnEttSettMedEksterneIdentifikatorer() {
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPSAK", "1"));
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPSAK", "2"));
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPSAK", "3"));
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPSAK", "4"));
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPSAK", "5"));

        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPTILBAKE", "1"));
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPTILBAKE", "3"));
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPTILBAKE", "5"));
    }




}
