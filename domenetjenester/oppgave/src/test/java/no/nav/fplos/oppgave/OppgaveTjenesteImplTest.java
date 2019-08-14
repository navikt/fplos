package no.nav.fplos.oppgave;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProviderImpl;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjenesteImpl;
import no.nav.fplos.person.api.TpsTjeneste;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OppgaveTjenesteImplTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepositoryProvider repositoryProvider = new OppgaveRepositoryProviderImpl(entityManager);
    private final OppgaveRepository oppgaveRepository = repositoryProvider.getOppgaveRepository();
    private TpsTjeneste tpsTjeneste = mock(TpsTjeneste.class);
    private AvdelingslederTjeneste avdelingslederTjeneste = new AvdelingslederTjenesteImpl(repositoryProvider);
    private AnsattTjeneste ansattTjeneste = mock(AnsattTjeneste.class);
    private OppgaveTjenesteImpl oppgaveTjeneste = new OppgaveTjenesteImpl(repositoryProvider, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste);

    private static String AVDELING_DRAMMEN_ENHET = "4806";

    private Oppgave førstegangOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private Oppgave klageOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.KLAGE).build();
    private Oppgave innsynOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.INNSYN).build();
    private Avdeling avdelingDrammen = null;
    private String begrunnelse = "Test";


    @Before
    public void setup(){
        List<Avdeling> avdelings = repoRule.getRepository().hentAlle(Avdeling.class);
        avdelingDrammen = avdelings.stream().filter(avdeling -> AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();
        entityManager.flush();
    }


    private Long leggeInnEtSettMedOppgaver(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.builder().medNavn("OPPRETTET").medSortering(KøSortering.OPPRETT_BEHANDLING).medAvdeling(avdelingDrammen).build();
        oppgaveRepository.lagre(oppgaveFiltrering);
        oppgaveRepository.lagre(førstegangOppgave);
        oppgaveRepository.lagre(klageOppgave);
        oppgaveRepository.lagre(innsynOppgave);
        entityManager.refresh(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    private Long leggeInnEtSettMedAndreKriterierOppgaver(){
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.builder().medNavn("OPPRETTET").medSortering(KøSortering.OPPRETT_BEHANDLING).medAvdeling(avdelingDrammen).build();
        oppgaveRepository.lagre(oppgaveFiltrering);
        leggtilOppgaveMedEkstraEgenskaper(førstegangOppgave, AndreKriterierType.TIL_BESLUTTER);
        leggtilOppgaveMedEkstraEgenskaper(førstegangOppgave, AndreKriterierType.PAPIRSØKNAD);
        leggtilOppgaveMedEkstraEgenskaper(klageOppgave, AndreKriterierType.PAPIRSØKNAD);
        oppgaveRepository.lagre(innsynOppgave);
        entityManager.refresh(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    private void leggtilOppgaveMedEkstraEgenskaper(Oppgave oppgave, AndreKriterierType andreKriterierType){
        oppgaveRepository.lagre(oppgave);
        oppgaveRepository.refresh(oppgave);
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, andreKriterierType));
    }

    private List<OppgaveFiltrering> leggInnEtSettMedLister(int antallLister){
        List<OppgaveFiltrering> oppgaveFiltrerings = new ArrayList<>();
        for(int i = 0; i< antallLister; i++) {
            OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.builder().medNavn("Test " + i).medSortering(KøSortering.BEHANDLINGSFRIST).medAvdeling(avdelingDrammen).build();
            entityManager.persist(oppgaveFiltrering);
            oppgaveFiltrerings.add(oppgaveFiltrering);
        }
        entityManager.flush();
        return oppgaveFiltrerings;
    }

    @Test
    public void testEnFiltreringpåBehandlingstype(){
        Long listeId = leggeInnEtSettMedOppgaver();
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.FØRSTEGANGSSØKNAD, true);
        List<Oppgave> oppgaver = oppgaveTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(1);
    }

    @Test
    public void testToFiltreringerpåBehandlingstype(){
        Long listeId = leggeInnEtSettMedOppgaver();
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.FØRSTEGANGSSØKNAD, true);
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.KLAGE, true);
        List<Oppgave> oppgaver = oppgaveTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(2);
    }

    @Test
    public void testFiltreringerpåAndreKriteriertype(){
        Long listeId = leggeInnEtSettMedAndreKriterierOppgaver();
        avdelingslederTjeneste.endreFiltreringAndreKriterierTypeType(listeId, AndreKriterierType.TIL_BESLUTTER, true, true);
        avdelingslederTjeneste.endreFiltreringAndreKriterierTypeType(listeId, AndreKriterierType.PAPIRSØKNAD, true, true);
        List<Oppgave> oppgaver = oppgaveTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(1);
    }

    @Test
    public void testUtenFiltreringpåBehandlingstype(){
        Long oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        List<Oppgave> oppgaver = oppgaveTjeneste.hentOppgaver(oppgaveFiltreringId);
        assertThat(oppgaver).hasSize(3);
    }

    @Test
    public void hentOppgaverSortertPåOpprettet() {
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(9,8, 10);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(10,0, 9);
        Oppgave tredjeOppgave = opprettOgLargeOppgaveTilSortering(8,9, 8);
        Oppgave fjerdeOppgave = opprettOgLargeOppgaveTilSortering(0,10, 0);

        OppgaveFiltrering opprettet = OppgaveFiltrering.builder().medNavn("OPPRETTET").medSortering(KøSortering.OPPRETT_BEHANDLING).medAvdeling(avdelingDrammen).build();
        oppgaveRepository.lagre(opprettet);

        List<Oppgave> oppgaves = oppgaveTjeneste.hentOppgaver(opprettet.getId());
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }

    @Test
    public void hentOppgaverSortertPåFrist() {
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(8,9, 8);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(0,10, 0);
        Oppgave tredjeOppgave = opprettOgLargeOppgaveTilSortering(9,8, 10);
        Oppgave fjerdeOppgave = opprettOgLargeOppgaveTilSortering(10,0, 9);

        OppgaveFiltrering frist = OppgaveFiltrering.builder().medNavn("FRIST").medSortering(KøSortering.BEHANDLINGSFRIST).medAvdeling(avdelingDrammen).build();
        oppgaveRepository.lagre(frist);

        List<Oppgave> oppgaves = oppgaveTjeneste.hentOppgaver(frist.getId());
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }

    @Test
    public void hentOppgaverSortertPåFørsteStonadsdag() {
        Oppgave fjerdeOppgave = opprettOgLargeOppgaveTilSortering(10,0, 0);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(8,9, 10);
        Oppgave tredjeOppgave = opprettOgLargeOppgaveTilSortering(9,8, 8);
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(0,10, 9);

        OppgaveFiltrering førsteStønadsdag = OppgaveFiltrering.builder().medNavn("STØNADSDAG").medSortering(KøSortering.FORSTE_STONADSDAG).medAvdeling(avdelingDrammen).build();
        oppgaveRepository.lagre(førsteStønadsdag);

        List<Oppgave> oppgaves = oppgaveTjeneste.hentOppgaver(førsteStønadsdag.getId());
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }


    private Oppgave opprettOgLargeOppgaveTilSortering(int dagerSidenOpprettet, int dagersidenBehandlingsFristGikkUt, int førsteStønadsdag){
        Oppgave oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medBehandlingOpprettet(LocalDateTime.now().minusDays(dagerSidenOpprettet))
                .medBehandlingsfrist(LocalDateTime.now().minusDays(dagersidenBehandlingsFristGikkUt))
                .medForsteStonadsdag(LocalDate.now().minusDays(førsteStønadsdag))
                .build();
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    @Test
    public void hentAlleOppgaveFiltrering(){
        List<OppgaveFiltrering> lagtInnLister = leggInnEtSettMedLister(3);
        Saksbehandler saksbehandler = new Saksbehandler("1234567");
        entityManager.persist(saksbehandler);
        entityManager.flush();

        avdelingslederTjeneste.leggSaksbehandlerTilOppgaveFiltrering(lagtInnLister.get(0).getId(), saksbehandler.getSaksbehandlerIdent());
        avdelingslederTjeneste.leggSaksbehandlerTilOppgaveFiltrering(lagtInnLister.get(2).getId(), saksbehandler.getSaksbehandlerIdent());
        entityManager.refresh(saksbehandler);

        List<OppgaveFiltrering> oppgaveFiltrerings = oppgaveTjeneste.hentAlleOppgaveFiltrering(saksbehandler.getSaksbehandlerIdent());
        assertThat(oppgaveFiltrerings).contains(lagtInnLister.get(0), lagtInnLister.get(2));
        assertThat(oppgaveFiltrerings).doesNotContain(lagtInnLister.get(1));
    }

    @Test
    public void testReservasjon() {
        Long oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        assertThat(oppgaveTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(3);
        assertThat(oppgaveTjeneste.hentReserverteOppgaver()).hasSize(0);
        assertThat(oppgaveTjeneste.hentSisteReserverteOppgaver()).hasSize(0);

        oppgaveTjeneste.reserverOppgave(førstegangOppgave.getId());
        assertThat(oppgaveTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(2);
        assertThat(oppgaveTjeneste.hentReserverteOppgaver()).hasSize(1);
        assertThat(oppgaveTjeneste.hentReserverteOppgaver().get(0).getReservertTil().until(LocalDateTime.now().plusHours(2), MINUTES)).isLessThan(2);
        assertThat(oppgaveTjeneste.hentSisteReserverteOppgaver()).hasSize(1);

        oppgaveTjeneste.forlengReservasjonPåOppgave(førstegangOppgave.getId());
        assertThat(oppgaveTjeneste.hentReserverteOppgaver().get(0).getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);

        oppgaveTjeneste.frigiOppgave(førstegangOppgave.getId(), begrunnelse);
        assertThat(oppgaveTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(3);
        assertThat(oppgaveTjeneste.hentReserverteOppgaver()).hasSize(0);
        assertThat(oppgaveTjeneste.hentSisteReserverteOppgaver()).hasSize(1);
        assertThat(oppgaveTjeneste.hentSisteReserverteOppgaver().get(0).getReservasjon().getBegrunnelse()).isEqualTo(begrunnelse);
    }

    @Test
    public void testOppgaverForandret(){
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(8,9, 10);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(0,10, 10);
        List<Long> oppgaveIder = Arrays.asList(førsteOppgave.getId(), andreOppgave.getId());
        assertThat(oppgaveTjeneste.harForandretOppgaver(oppgaveIder)).isFalse();
        oppgaveTjeneste.reserverOppgave(førsteOppgave.getId());
        assertThat(oppgaveTjeneste.harForandretOppgaver(oppgaveIder)).isTrue();
    }

    @Test
    public void testHentSaksbehandlerNavnOgAvdelinger(){
        String saksbehandler1Ident = "1234567";
        String saksbehandler2Ident = "9876543";
        String saksbehandler3Ident = "1234";

        Saksbehandler saksbehandler1 = new Saksbehandler(saksbehandler1Ident);
        Saksbehandler saksbehandler2 = new Saksbehandler(saksbehandler2Ident);
        entityManager.persist(saksbehandler1);
        entityManager.persist(saksbehandler2);
        entityManager.flush();

        List<OppgaveFiltrering> lagtInnLister = leggInnEtSettMedLister(1);

        avdelingslederTjeneste.leggSaksbehandlerTilOppgaveFiltrering(lagtInnLister.get(0).getId(), saksbehandler1.getSaksbehandlerIdent());
        entityManager.refresh(saksbehandler1);

        assertThat(oppgaveTjeneste.hentSaksbehandlerNavnOgAvdelinger(saksbehandler3Ident)).isNull();
        assertThat(oppgaveTjeneste.hentSaksbehandlerNavnOgAvdelinger(saksbehandler2Ident)).isNull();
    }
}
