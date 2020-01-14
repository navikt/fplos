package no.nav.fplos.avdelingsleder;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.OrganisasjonRessursEnhetTjeneste;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AvdelingslederSaksbehandlerTjenesteImplTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private final OrganisasjonRepository organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);

    private OrganisasjonRessursEnhetTjeneste organisasjonRessursEnhetTjeneste = Mockito.mock(OrganisasjonRessursEnhetTjeneste.class);
    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste = new AvdelingslederSaksbehandlerTjenesteImpl(oppgaveRepository, organisasjonRepository, organisasjonRessursEnhetTjeneste);
    private Avdeling avdelingDrammen = null;
    private static String NY_SAKSBEHANDLER_IDENT = "zNySaksbehandler";
    private String AVDELING_DRAMMEN_ENHET;

    @Before
    public void setup(){
        List<Avdeling> avdelings = repoRule.getRepository().hentAlle(Avdeling.class);
        avdelingDrammen = avdelings.stream().filter(avdeling -> Avdeling.AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();
        AVDELING_DRAMMEN_ENHET = avdelingDrammen.getAvdelingEnhet();
    }

    @Test
    public void testHentSaksbehandlere() {
        List<Saksbehandler> saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggTilSaksbehandler(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isNotEmpty();
        assertThat(saksbehandlers.get(0).getId()).isNotNull();
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
    }

    @Test
    public void testLagreNySaksbehandler() {
        List<Saksbehandler> saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers).isEmpty();
        avdelingslederSaksbehandlerTjeneste.leggTilSaksbehandler(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
    }

    @Test
    public void testSlettSaksbehandler() {
        avdelingslederSaksbehandlerTjeneste.leggTilSaksbehandler(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        List<Saksbehandler> saksbehandlers = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET);
        assertThat(saksbehandlers.get(0).getSaksbehandlerIdent()).isEqualTo(NY_SAKSBEHANDLER_IDENT.toUpperCase());
        assertThat(saksbehandlers.get(0).getAvdelinger()).hasSize(1);
        avdelingslederSaksbehandlerTjeneste.slettSaksbehandler(NY_SAKSBEHANDLER_IDENT, AVDELING_DRAMMEN_ENHET);
        assertThat(avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(AVDELING_DRAMMEN_ENHET)).isEmpty();
    }
}