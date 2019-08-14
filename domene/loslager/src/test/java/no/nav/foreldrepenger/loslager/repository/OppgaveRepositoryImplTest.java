package no.nav.foreldrepenger.loslager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.vedtak.felles.testutilities.db.Repository;

public class OppgaveRepositoryImplTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final Repository repository = repoRule.getRepository();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepositoryProvider repositoryProvider = new OppgaveRepositoryProviderImpl(entityManager);
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private Avdeling avdelingDrammen = null;
    private static String AVDELING_DRAMMEN_ENHET = "4806";
    private Long AVDELING_DRAMMEN;

    private static String AVDELING_ANNET_ENHET = "4000";

    @Before
    public void setup(){
        List<Avdeling> avdelings = repository.hentAlle(Avdeling.class);
        avdelingDrammen = avdelings.stream().filter(avdeling -> AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();
        AVDELING_DRAMMEN = avdelingDrammen.getId();
        entityManager.flush();
    }

    @Test
    public void testHentingAvOppgaver(){
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaves = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.UDEFINERT,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaves).hasSize(4);
        assertThat(oppgaveRepository.hentAntallOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.UDEFINERT,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),new ArrayList<>(), false, null, null, null, null))).isEqualTo(4);
        assertThat(oppgaves).first().hasFieldOrPropertyWithValue("behandlendeEnhet", AVDELING_DRAMMEN_ENHET);
    }

    @Test
    public void testEkskluderingOgInkluderingAvOppgaver(){
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.UDEFINERT,
                new ArrayList<>(), new ArrayList<>(), java.util.Arrays.asList(AndreKriterierType.TIL_BESLUTTER,AndreKriterierType.PAPIRSØKNAD),new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.UDEFINERT, new ArrayList<>(), new ArrayList<>(),
                java.util.Arrays.asList(AndreKriterierType.TIL_BESLUTTER),new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.UDEFINERT, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                java.util.Arrays.asList(AndreKriterierType.TIL_BESLUTTER,AndreKriterierType.PAPIRSØKNAD), false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.UDEFINERT, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(),java.util.Arrays.asList(AndreKriterierType.TIL_BESLUTTER), false, null, null, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.UDEFINERT, new ArrayList<>(), new ArrayList<>(),
                java.util.Arrays.asList(AndreKriterierType.PAPIRSØKNAD),java.util.Arrays.asList(AndreKriterierType.TIL_BESLUTTER), false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);
        int andtallOppgaver = oppgaveRepository.hentAntallOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.UDEFINERT, new ArrayList<>(), new ArrayList<>(),
                java.util.Arrays.asList(AndreKriterierType.PAPIRSØKNAD),java.util.Arrays.asList(AndreKriterierType.TIL_BESLUTTER), false, null, null, null, null));
        assertThat(andtallOppgaver).isEqualTo(1);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverIntervall() {
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaves = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, 1L, 10L));
        assertThat(oppgaves).hasSize(2);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverBareFomDato() {
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaves = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, 15L, null));
        assertThat(oppgaves).hasSize(1);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverBareTomDato() {
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaves = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, KøSortering.BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, null, 15L));
        assertThat(oppgaves).hasSize(4);
    }


    private void lagStandardSettMedOppgaver() {
        Oppgave andreOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingOpprettet(LocalDateTime.now().minusDays(9)).medBehandlingsfrist(LocalDateTime.now().plusDays(5)).build();
        Oppgave førsteOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingOpprettet(LocalDateTime.now().minusDays(10)).medBehandlingsfrist(LocalDateTime.now().plusDays(10)).build();
        Oppgave tredjeOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingOpprettet(LocalDateTime.now().minusDays(8)).medBehandlingsfrist(LocalDateTime.now().plusDays(15)).build();
        Oppgave fjerdeOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingOpprettet(LocalDateTime.now()).medBehandlingsfrist(LocalDateTime.now()).build();
        repository.lagre(tredjeOppgave);
        repository.lagre(førsteOppgave);
        repository.lagre(andreOppgave);
        repository.lagre(fjerdeOppgave);
        repository.lagre(new OppgaveEgenskap(førsteOppgave, AndreKriterierType.PAPIRSØKNAD));
        repository.lagre(new OppgaveEgenskap(andreOppgave, AndreKriterierType.TIL_BESLUTTER));
        repository.lagre(new OppgaveEgenskap(tredjeOppgave, AndreKriterierType.PAPIRSØKNAD));
        repository.lagre(new OppgaveEgenskap(tredjeOppgave, AndreKriterierType.TIL_BESLUTTER));
        repository.flush();
    }

    @Test
    public void testReservering(){
        Oppgave oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingOpprettet(LocalDateTime.now().minusDays(10)).build();
        repository.lagre(oppgave);
        repository.flush();
        Reservasjon reservertOppgave = oppgaveRepository.hentReservasjon(oppgave.getId());
        assertThat(reservertOppgave).isNotNull();
    }

    @Test
    public void hentAlleLister(){
        OppgaveFiltrering førsteOppgaveFiltrering = OppgaveFiltrering.builder().medNavn("OPPRETTET").medSortering(KøSortering.OPPRETT_BEHANDLING).medAvdeling(avdelingDrammen).build();
        OppgaveFiltrering andreOppgaveFiltrering = OppgaveFiltrering.builder().medNavn("BEHANDLINGSFRIST").medSortering(KøSortering.BEHANDLINGSFRIST).medAvdeling(avdelingDrammen).build();

        repository.lagre(førsteOppgaveFiltrering);
        repository.lagre(andreOppgaveFiltrering);
        repository.flush();

        List<OppgaveFiltrering> lister = oppgaveRepository.hentAlleLister(AVDELING_DRAMMEN);

        assertThat(lister).extracting(OppgaveFiltrering::getNavn).contains("OPPRETTET", "BEHANDLINGSFRIST");
        assertThat(lister).extracting(OppgaveFiltrering::getAvdeling).contains(avdelingDrammen);
        assertThat(lister).extracting(OppgaveFiltrering::getSortering).contains(KøSortering.BEHANDLINGSFRIST,KøSortering.OPPRETT_BEHANDLING);
    }

    @Test
    public void lagreOppgaveHvisForskjelligEnhet(){
        Oppgave oppgave = lagOppgave(AVDELING_DRAMMEN_ENHET);
        Oppgave oppgaveKommerPåNytt = lagOppgave(AVDELING_ANNET_ENHET);
        oppgaveRepository.opprettOppgave(oppgave);
        assertThat(repository.hentAlle(Oppgave.class)).hasSize(1);
        oppgaveRepository.opprettOppgave(oppgaveKommerPåNytt);
        assertThat(repository.hentAlle(Oppgave.class)).hasSize(2);
    }

    @Test
    public void lagreOppgaveHvisAvsluttetFraFør(){
        Oppgave oppgave = lagOppgave(AVDELING_DRAMMEN_ENHET);
        Oppgave oppgaveKommerPåNytt = lagOppgave(AVDELING_DRAMMEN_ENHET);
        oppgaveRepository.opprettOppgave(oppgave);
        assertThat(repository.hentAlle(Oppgave.class)).hasSize(1);
        oppgaveRepository.avsluttOppgave(oppgave.getBehandlingId());
        oppgaveRepository.opprettOppgave(oppgaveKommerPåNytt);
        assertThat(repository.hentAlle(Oppgave.class)).hasSize(2);
    }

    private Oppgave lagOppgave(String behandlendeEnhet){
        return Oppgave.builder().medBehandlingId(1L).medFagsakSaksnummer(1337L)
                .medAktorId(5000000L).medBehandlendeEnhet(behandlendeEnhet)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER).medAktiv(true).medBehandlingsfrist(LocalDateTime.now()).build();
    }
}
