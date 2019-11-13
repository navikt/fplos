package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.EksternIdentifikator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EksternIdentifikatorRepositoryImplTest {
    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final EksternIdentifikatorRepository eksternIdentifikatorRepository = new EksternIdentifikatorRepositoryImpl(entityManager);

    @Before
    public void setup(){
        entityManager.flush();
    }

    @Test
    public void testFinnEksisterendeIdentifikator(){
        leggInnEttSettMedEksterneIdentifikatorer();
        Optional<EksternIdentifikator> eksternId = eksternIdentifikatorRepository.finnIdentifikator("FPSAK", "2");
        assertTrue(eksternId.isPresent());
        assertNotNull(eksternId.get().getId());
    }

    @Test
    public void testFinnerIkkeIdentifikator(){
        leggInnEttSettMedEksterneIdentifikatorer();
        Optional<EksternIdentifikator> eksternId = eksternIdentifikatorRepository.finnIdentifikator("FPSAK", "6");
        assertTrue(eksternId.isEmpty());
        assertFalse(eksternId.isPresent());
    }

    @Test
    public void testFinnEllerOpprettEksternId(){
        leggInnEttSettMedEksterneIdentifikatorer();
        EksternIdentifikator eksisterendeEksternId = eksternIdentifikatorRepository.finnEllerOpprettEksternId("FPSAK", "2");
        assertNotNull(eksisterendeEksternId);
        assertNotNull(eksisterendeEksternId.getId());

        EksternIdentifikator nyEksternId = eksternIdentifikatorRepository.finnEllerOpprettEksternId("FPSAK", "6");
        assertNotNull(nyEksternId);
        assertNotNull(nyEksternId.getId());
    }

    @Test
    public void testIdentifikatorMedSammeEksternRefFraUlikeSystemErUlike(){
        leggInnEttSettMedEksterneIdentifikatorer();
        EksternIdentifikator fpsakEksternId = eksternIdentifikatorRepository.finnEllerOpprettEksternId("FPSAK", "3");
        assertNotNull(fpsakEksternId);
        assertNotNull(fpsakEksternId.getId());
        EksternIdentifikator fptilbakeEksternId = eksternIdentifikatorRepository.finnEllerOpprettEksternId("FPTILBAKE", "3");
        assertNotNull(fptilbakeEksternId);
        assertNotNull(fptilbakeEksternId.getId());
        assertNotEquals(fpsakEksternId.getId(),fptilbakeEksternId.getId());
    }

    @Test
    public void testFinnIdentifikatorOgFinnEllerOpprettEksternIdReturnererSammeVerdiForEksisterendeEksternId() {
        leggInnEttSettMedEksterneIdentifikatorer();

        Optional<EksternIdentifikator> eksisterendeEksternId_1 = eksternIdentifikatorRepository.finnIdentifikator("FPSAK", "2");
        assertTrue(eksisterendeEksternId_1.isPresent());
        assertNotNull(eksisterendeEksternId_1.get().getId());

        EksternIdentifikator eksisterendeEksternId_2 = eksternIdentifikatorRepository.finnEllerOpprettEksternId("FPSAK", "2");
        assertNotNull(eksisterendeEksternId_2);
        assertNotNull(eksisterendeEksternId_2.getId());

        assertEquals(eksisterendeEksternId_1.get().getId(), eksisterendeEksternId_2.getId());

    }

    @Test(expected = PersistenceException.class)
    public void testDobbeltlagringFeiler() {
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPSAK", "1"));
        eksternIdentifikatorRepository.lagre(new EksternIdentifikator("FPSAK", "1"));
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
