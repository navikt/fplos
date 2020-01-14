package no.nav.foreldrepenger.loslager.repository;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.vedtak.felles.testutilities.db.Repository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static no.nav.foreldrepenger.loslager.oppgave.KøSortering.BEHANDLINGSFRIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OppgaveRepositoryImplTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final Repository repository = repoRule.getRepository();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private Avdeling avdelingDrammen = null;
    private static String AVDELING_DRAMMEN_ENHET = "4806";
    private Long AVDELING_DRAMMEN;

    private static UUID uuid1 = UUID.nameUUIDFromBytes("uuid_1".getBytes());
    private static UUID uuid2 = UUID.nameUUIDFromBytes("uuid_2".getBytes());
    private static UUID uuid3 = UUID.nameUUIDFromBytes("uuid_3".getBytes());
    private static UUID uuid4 = UUID.nameUUIDFromBytes("uuid_4".getBytes());
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
        List<Oppgave> oppgaves = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaves).hasSize(4);
        assertThat(oppgaveRepository.hentAntallOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),new ArrayList<>(), false, null, null, null, null))).isEqualTo(4);
        assertThat(oppgaves).first().hasFieldOrPropertyWithValue("behandlendeEnhet", AVDELING_DRAMMEN_ENHET);
    }

    @Test
    public void testHentingAvEventerVedEksternId(){
        lagStandardSettMedOppgaver();
        List<OppgaveEventLogg> events = oppgaveRepository.hentEventerForEksternId(uuid1);
        assertThat(events).hasSize(1);
        assertEquals(uuid1, events.get(0).getEksternId());
    }

    private Long setupOppgaveMedEgenskaper(AndreKriterierType... kriterier) {
        List<BaseEntitet> entiteter = new ArrayList<>();
        Long saksnummer = (long) (Math.random() * 10000);
        Oppgave oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medFagsakSaksnummer(saksnummer).build();
        entiteter.add(oppgave);
        for (var kriterie : kriterier) {
            entiteter.add(new OppgaveEgenskap(oppgave, kriterie));
        }
        repository.lagre(entiteter);
        repository.flush();
        return saksnummer;
    }

    @Test
    public void testLagringAvOppgaveEgenskaper() {
        Long saksnummerOppgaveEn = setupOppgaveMedEgenskaper(AndreKriterierType.UTLANDSSAK, AndreKriterierType.PAPIRSØKNAD);
        List<Oppgave> lagredeOppgaver = oppgaveRepository.hentOppgaverForSaksnummer(saksnummerOppgaveEn);
        List<OppgaveEgenskap> lagredeEgenskaper = oppgaveRepository.hentOppgaveEgenskaper(lagredeOppgaver.get(0).getId());
        List<AndreKriterierType> lagredeKriterier = lagredeEgenskaper.stream()
                .map(OppgaveEgenskap::getAndreKriterierType)
                .collect(Collectors.toList());

        assertThat(lagredeKriterier).containsExactlyInAnyOrder(AndreKriterierType.UTLANDSSAK, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void testOppgaveSpørringMedEgenskaperfiltrering() {
        Long saksnummerHit = setupOppgaveMedEgenskaper(AndreKriterierType.UTLANDSSAK, AndreKriterierType.UTBETALING_TIL_BRUKER);
        Long saksnummerMiss = setupOppgaveMedEgenskaper(AndreKriterierType.PAPIRSØKNAD);
        OppgavespørringDto oppgaveQuery = new OppgavespørringDto(AVDELING_DRAMMEN,
                BEHANDLINGSFRIST,
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(AndreKriterierType.UTLANDSSAK), // inkluderes
                //Collections.emptyList(),
                List.of(AndreKriterierType.SOKT_GRADERING), // ekskluderes
                //Collections.emptyList(), //ekskluderes
                false,
                null,
                null,
                null,
                null);
        //List<Oppgave> alleOppgaver = oppgaveRepository.hentOppgaver(alleOppgaverQuery);
        List<Oppgave> oppgaver = oppgaveRepository.hentOppgaver(oppgaveQuery);
        assertThat(oppgaver.size()).isEqualTo(1);
        assertThat(oppgaver.get(0).getFagsakSaksnummer()).isEqualTo(saksnummerHit);
    }

    @Test
    public void testEkskluderingOgInkluderingAvOppgaver(){
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, null,
                new ArrayList<>(), new ArrayList<>(), Arrays.asList(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD),
                new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, null, new ArrayList<>(), new ArrayList<>(),
                Arrays.asList(AndreKriterierType.TIL_BESLUTTER), new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                Arrays.asList(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD), // ekskluder andreKriterierType
                false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, null, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(),
                Arrays.asList(AndreKriterierType.TIL_BESLUTTER),  // ekskluderAndreKriterierType
                false, null, null, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, null, new ArrayList<>(), new ArrayList<>(),
                Arrays.asList(AndreKriterierType.PAPIRSØKNAD),Arrays.asList(AndreKriterierType.TIL_BESLUTTER), false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);
        int antallOppgaver = oppgaveRepository.hentAntallOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, null, new ArrayList<>(), new ArrayList<>(),
                Arrays.asList(AndreKriterierType.PAPIRSØKNAD),Arrays.asList(AndreKriterierType.TIL_BESLUTTER), false, null, null, null, null));
        assertThat(antallOppgaver).isEqualTo(1);

        int antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN);
        assertThat(antallOppgaverForAvdeling).isEqualTo(4);

    }

    @Test
    public void testAntallOppgaverForAvdeling(){
        int antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN);
        assertThat(antallOppgaverForAvdeling).isEqualTo(0);
        lagStandardSettMedOppgaver();
        antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN);
        assertThat(antallOppgaverForAvdeling).isEqualTo(4);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverIntervall() {
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaves = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, 1L, 10L));
        assertThat(oppgaves).hasSize(2);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverBareFomDato() {
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaves = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, 15L, null));
        assertThat(oppgaves).hasSize(1);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverBareTomDato() {
        lagStandardSettMedOppgaver();
        List<Oppgave> oppgaves = oppgaveRepository.hentOppgaver(new OppgavespørringDto(AVDELING_DRAMMEN, BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, null, 15L));
        assertThat(oppgaves).hasSize(4);
    }


    private void lagStandardSettMedOppgaver() {
        Oppgave andreOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medEksternId(uuid1).medFagsakSaksnummer(111L).medBehandlingOpprettet(LocalDateTime.now().minusDays(9)).medBehandlingsfrist(LocalDateTime.now().plusDays(5)).build();
        Oppgave førsteOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medEksternId(uuid2).medFagsakSaksnummer(222L).medBehandlingOpprettet(LocalDateTime.now().minusDays(10)).medBehandlingsfrist(LocalDateTime.now().plusDays(10)).build();
        Oppgave tredjeOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medEksternId(uuid3).medFagsakSaksnummer(333L).medBehandlingOpprettet(LocalDateTime.now().minusDays(8)).medBehandlingsfrist(LocalDateTime.now().plusDays(15)).build();
        Oppgave fjerdeOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medEksternId(uuid4).medFagsakSaksnummer(444L).medBehandlingOpprettet(LocalDateTime.now()).medBehandlingsfrist(LocalDateTime.now()).build();
        repository.lagre(førsteOppgave);
        repository.lagre(andreOppgave);
        repository.lagre(tredjeOppgave);
        repository.lagre(fjerdeOppgave);
        repository.lagre(new OppgaveEgenskap(førsteOppgave, AndreKriterierType.PAPIRSØKNAD));
        repository.lagre(new OppgaveEgenskap(andreOppgave, AndreKriterierType.TIL_BESLUTTER, "Jodajoda"));
        repository.lagre(new OppgaveEgenskap(tredjeOppgave, AndreKriterierType.PAPIRSØKNAD));
        repository.lagre(new OppgaveEgenskap(tredjeOppgave, AndreKriterierType.TIL_BESLUTTER));
        repository.lagre(new OppgaveEventLogg(uuid1, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, AVDELING_DRAMMEN_ENHET,1L ));
        repository.lagre(new OppgaveEventLogg(uuid2, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, AVDELING_DRAMMEN_ENHET,2L));
        repository.lagre(new OppgaveEventLogg(uuid3, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, AVDELING_DRAMMEN_ENHET,3L));
        repository.lagre(new OppgaveEventLogg(uuid3, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, AVDELING_DRAMMEN_ENHET,3L));
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
        OppgaveFiltrering andreOppgaveFiltrering = OppgaveFiltrering.builder().medNavn("BEHANDLINGSFRIST").medSortering(BEHANDLINGSFRIST).medAvdeling(avdelingDrammen).build();

        repository.lagre(førsteOppgaveFiltrering);
        repository.lagre(andreOppgaveFiltrering);
        repository.flush();

        List<OppgaveFiltrering> lister = oppgaveRepository.hentAlleLister(AVDELING_DRAMMEN);

        assertThat(lister).extracting(OppgaveFiltrering::getNavn).contains("OPPRETTET", "BEHANDLINGSFRIST");
        assertThat(lister).extracting(OppgaveFiltrering::getAvdeling).contains(avdelingDrammen);
        assertThat(lister).extracting(OppgaveFiltrering::getSortering).contains(BEHANDLINGSFRIST,KøSortering.OPPRETT_BEHANDLING);
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

    @Test
    public void avsluttSistOpprettetOppgave() {
        Oppgave første = lagOppgave(AVDELING_DRAMMEN_ENHET);
        oppgaveRepository.opprettOppgave(første);
        Oppgave siste = lagOppgave(AVDELING_DRAMMEN_ENHET);
        oppgaveRepository.opprettOppgave(siste);
        assertThat(repository.hentAlle(Oppgave.class)).hasSize(2);
        assertThat(første()).isEqualTo(første);
        assertThat(siste().getAktiv()).isTrue();
        assertThat(første().getOpprettetTidspunkt()).isBefore(siste().getOpprettetTidspunkt());

        oppgaveRepository.avsluttOppgave(første.getBehandlingId());
        assertThat(første().getAktiv()).isTrue();
        assertThat(siste().getAktiv()).isFalse();
    }

    @Test
    public void fårTomtSvarFraOppgaveFiltrering() {
        OppgaveFiltrering filtrering = oppgaveRepository.hentListe(0L);
        assertNull(filtrering);
    }


    private Oppgave første() {
        return repository.hentAlle(Oppgave.class).get(0);
    }

    private Oppgave siste() {
        return repository.hentAlle(Oppgave.class).get(1);
    }

    private Oppgave lagOppgave(String behandlendeEnhet){
        return Oppgave.builder().medBehandlingId(1L).medFagsakSaksnummer(1337L)
                .medAktorId(5000000L).medBehandlendeEnhet(behandlendeEnhet)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medAktiv(true)
                .medBehandlingsfrist(LocalDateTime.now())
                .build();
    }
}
