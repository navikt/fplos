package no.nav.foreldrepenger.los.oppgave;

import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.BEHANDLINGSFRIST;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.FEILUTBETALINGSTART;
import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.dbstøtte.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class OppgaveRepositoryTest {

    private static final BehandlingId behandlingId1 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_1".getBytes()));
    private static final BehandlingId behandlingId2 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_2".getBytes()));
    private static final BehandlingId behandlingId3 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_3".getBytes()));
    private static final BehandlingId behandlingId4 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_4".getBytes()));

    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;


    @BeforeEach
    public void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
    }

    @Test
    public void testHentingAvOppgaver() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveRepository.hentOppgaver(oppgaverForDrammenSpørring());
        assertThat(oppgaves).hasSize(4);
        assertThat(oppgaveRepository.hentAntallOppgaver(oppgaverForDrammenSpørring())).isEqualTo(4);
        assertThat(oppgaves).first().hasFieldOrPropertyWithValue("behandlendeEnhet", AVDELING_DRAMMEN_ENHET);
    }

    private Oppgavespørring oppgaverForDrammenSpørring() {
        return new Oppgavespørring(avdelingIdForDrammen(), null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false,
                null, null, null, null);
    }

    private Long avdelingIdForDrammen() {
        return avdelingForDrammen().getId();
    }

    private Avdeling avdelingForDrammen() {
        return DBTestUtil.hentAlle(entityManager, Avdeling.class).stream()
                .filter(a -> a.getAvdelingEnhet().equals(AVDELING_DRAMMEN_ENHET))
                .findAny().orElseThrow();
    }

    @Test
    public void testHentingAvEventerVedBehandlingId() {
        lagStandardSettMedOppgaver();
        var event = oppgaveRepository.hentOppgaveEventer(behandlingId1).get(0);
        assertThat(behandlingId1).isEqualTo(event.getBehandlingId());
    }

    private Long setupOppgaveMedEgenskaper(AndreKriterierType... kriterier) {
        Long saksnummer = (long) (Math.random() * 10000);
        var oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medFagsakSaksnummer(saksnummer).build();
        entityManager.persist(oppgave);
        for (var kriterie : kriterier) {
            entityManager.persist(new OppgaveEgenskap(oppgave, kriterie));
        }
        entityManager.flush();
        return saksnummer;
    }

    @Test
    public void testLagringAvOppgaveEgenskaper() {
        var saksnummerOppgaveEn = setupOppgaveMedEgenskaper(AndreKriterierType.UTLANDSSAK, AndreKriterierType.PAPIRSØKNAD);
        var lagredeOppgaver = oppgaveRepository.hentAktiveOppgaverForSaksnummer(List.of(saksnummerOppgaveEn));
        var lagredeEgenskaper = oppgaveRepository.hentOppgaveEgenskaper(lagredeOppgaver.get(0).getId());
        var lagredeKriterier = lagredeEgenskaper.stream()
                .map(OppgaveEgenskap::getAndreKriterierType)
                .collect(Collectors.toList());

        assertThat(lagredeKriterier).containsExactlyInAnyOrder(AndreKriterierType.UTLANDSSAK, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    public void testOppgaveSpørringMedEgenskaperfiltrering() {
        var saksnummerHit = setupOppgaveMedEgenskaper(AndreKriterierType.UTLANDSSAK, AndreKriterierType.UTBETALING_TIL_BRUKER);
        var oppgaveQuery = new Oppgavespørring(avdelingIdForDrammen(),
                BEHANDLINGSFRIST,
                List.of(),
                List.of(),
                List.of(AndreKriterierType.UTLANDSSAK), // inkluderes
                List.of(AndreKriterierType.SØKT_GRADERING), // ekskluderes
                false,
                null,
                null,
                null,
                null);
        var oppgaver = oppgaveRepository.hentOppgaver(oppgaveQuery);
        assertThat(oppgaver).hasSize(1);
        assertThat(oppgaver.get(0).getFagsakSaksnummer()).isEqualTo(saksnummerHit);
    }

    @Test
    public void testEkskluderingOgInkluderingAvOppgaver() {
        lagStandardSettMedOppgaver();
        var oppgaver = oppgaveRepository.hentOppgaver(new Oppgavespørring(avdelingIdForDrammen(), null,
                new ArrayList<>(), new ArrayList<>(), List.of(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD),
                new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveRepository.hentOppgaver(new Oppgavespørring(avdelingIdForDrammen(), null, new ArrayList<>(), new ArrayList<>(),
                List.of(AndreKriterierType.TIL_BESLUTTER), new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveRepository.hentOppgaver(new Oppgavespørring(avdelingIdForDrammen(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                List.of(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD), // ekskluder andreKriterierType
                false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveRepository.hentOppgaver(new Oppgavespørring(avdelingIdForDrammen(), null, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(),
                List.of(AndreKriterierType.TIL_BESLUTTER),  // ekskluderAndreKriterierType
                false, null, null, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveRepository.hentOppgaver(new Oppgavespørring(avdelingIdForDrammen(), null, new ArrayList<>(), new ArrayList<>(),
                List.of(AndreKriterierType.PAPIRSØKNAD), List.of(AndreKriterierType.TIL_BESLUTTER), false,
                null, null, null, null));
        assertThat(oppgaver).hasSize(1);
        var antallOppgaver = oppgaveRepository.hentAntallOppgaver(new Oppgavespørring(avdelingIdForDrammen(), null, new ArrayList<>(), new ArrayList<>(),
                List.of(AndreKriterierType.PAPIRSØKNAD), List.of(AndreKriterierType.TIL_BESLUTTER), false,
                null, null, null, null));
        assertThat(antallOppgaver).isEqualTo(1);

        var antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(avdelingIdForDrammen());
        assertThat(antallOppgaverForAvdeling).isEqualTo(4);

    }

    @Test
    public void testAntallOppgaverForAvdeling() {
        var antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(avdelingIdForDrammen());
        assertThat(antallOppgaverForAvdeling).isEqualTo(0);
        lagStandardSettMedOppgaver();
        antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(avdelingIdForDrammen());
        assertThat(antallOppgaverForAvdeling).isEqualTo(4);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverIntervall() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveRepository.hentOppgaver(new Oppgavespørring(avdelingIdForDrammen(), BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, 1L, 10L));
        assertThat(oppgaves).hasSize(2);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverBareFomDato() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveRepository.hentOppgaver(new Oppgavespørring(avdelingIdForDrammen(), BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, 15L, null));
        assertThat(oppgaves).hasSize(1);
    }

    @Test
    public void testFiltreringDynamiskAvOppgaverBareTomDato() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveRepository.hentOppgaver(new Oppgavespørring(avdelingIdForDrammen(), BEHANDLINGSFRIST,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, null, null, null, 15L));
        assertThat(oppgaves).hasSize(4);
    }


    private void lagStandardSettMedOppgaver() {
        var førsteOppgave = Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medBehandlingId(behandlingId1)
                .medFagsakSaksnummer(111L)
                .medBehandlingOpprettet(LocalDateTime.now().minusDays(10))
                .medBehandlingsfrist(LocalDateTime.now().plusDays(10))
                .build();
        var andreOppgave = Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medBehandlingId(behandlingId2)
                .medFagsakSaksnummer(222L)
                .medBehandlingOpprettet(LocalDateTime.now().minusDays(9))
                .medBehandlingsfrist(LocalDateTime.now().plusDays(5))
                .build();
        var tredjeOppgave = Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medBehandlingId(behandlingId3)
                .medFagsakSaksnummer(333L)
                .medBehandlingOpprettet(LocalDateTime.now().minusDays(8))
                .medBehandlingsfrist(LocalDateTime.now().plusDays(15))
                .build();
        var fjerdeOppgave = Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medBehandlingId(behandlingId4)
                .medFagsakSaksnummer(444L)
                .medBehandlingOpprettet(LocalDateTime.now())
                .medBehandlingsfrist(LocalDateTime.now())
                .build();
        entityManager.persist(førsteOppgave);
        entityManager.persist(andreOppgave);
        entityManager.persist(tredjeOppgave);
        entityManager.persist(fjerdeOppgave);
        entityManager.persist(new OppgaveEgenskap(førsteOppgave, AndreKriterierType.PAPIRSØKNAD));
        entityManager.persist(new OppgaveEgenskap(andreOppgave, AndreKriterierType.TIL_BESLUTTER, "Jodajoda"));
        entityManager.persist(new OppgaveEgenskap(tredjeOppgave, AndreKriterierType.PAPIRSØKNAD));
        entityManager.persist(new OppgaveEgenskap(tredjeOppgave, AndreKriterierType.TIL_BESLUTTER));
        entityManager.persist(new OppgaveEventLogg(behandlingId1, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(behandlingId2, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(behandlingId3, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(behandlingId3, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, AVDELING_DRAMMEN_ENHET));
        entityManager.flush();
    }

    @Test
    public void testReservering() {
        var oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingOpprettet(LocalDateTime.now().minusDays(10)).build();
        entityManager.persist(oppgave);
        entityManager.flush();
        var reservertOppgave = oppgaveRepository.hentReservasjon(oppgave.getId());
        assertThat(reservertOppgave).isNotNull();
    }

    @Test
    public void hentAlleLister() {
        var avdeling = avdelingForDrammen();
        var førsteOppgaveFiltrering = OppgaveFiltrering.builder().medNavn("OPPRETTET")
                .medSortering(KøSortering.OPPRETT_BEHANDLING)
                .medAvdeling(avdeling).build();
        var andreOppgaveFiltrering = OppgaveFiltrering.builder()
                .medNavn("BEHANDLINGSFRIST").medSortering(BEHANDLINGSFRIST)
                .medAvdeling(avdeling).build();

        entityManager.persist(førsteOppgaveFiltrering);
        entityManager.persist(andreOppgaveFiltrering);
        entityManager.flush();

        var lister = oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(avdelingIdForDrammen());

        assertThat(lister).extracting(OppgaveFiltrering::getNavn).contains("OPPRETTET", "BEHANDLINGSFRIST");
        assertThat(lister).extracting(OppgaveFiltrering::getAvdeling).contains(avdeling);
        assertThat(lister).extracting(OppgaveFiltrering::getSortering).contains(BEHANDLINGSFRIST, KøSortering.OPPRETT_BEHANDLING);
    }

    @Test
    public void lagreOppgaveHvisForskjelligEnhet() {
        var oppgave = lagOppgave(AVDELING_DRAMMEN_ENHET);
        var AVDELING_ANNET_ENHET = "4000";
        var oppgaveKommerPåNytt = lagOppgave(AVDELING_ANNET_ENHET);
        oppgaveRepository.opprettOppgave(oppgave);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(1);
        oppgaveRepository.opprettOppgave(oppgaveKommerPåNytt);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(2);
    }

    @Test
    public void lagreOppgaveHvisAvsluttetFraFør() {
        var oppgave = lagOppgave(AVDELING_DRAMMEN_ENHET);
        var oppgaveKommerPåNytt = lagOppgave(AVDELING_DRAMMEN_ENHET);
        oppgaveRepository.opprettOppgave(oppgave);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(1);
        oppgaveRepository.avsluttOppgaveForBehandling(oppgave.getBehandlingId());
        oppgaveRepository.opprettOppgave(oppgaveKommerPåNytt);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(2);
    }

    @Test
    public void skalKasteExceptionVedLukkingAvOppgaveDerDetFinnesFlereAktiveOppgaver() {
        var første = lagOppgave(AVDELING_DRAMMEN_ENHET);
        oppgaveRepository.opprettOppgave(første);
        var siste = lagOppgave(AVDELING_DRAMMEN_ENHET);
        oppgaveRepository.opprettOppgave(siste);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(2);
        assertThat(første()).isEqualTo(første);
        assertThat(siste().getAktiv()).isTrue();
        assertThat(første().getOpprettetTidspunkt()).isBefore(siste().getOpprettetTidspunkt());
        assertThrows(IllegalStateException.class, () -> oppgaveRepository.avsluttOppgaveForBehandling(første.getBehandlingId()));
    }

    @Test
    public void filtrerPåOpprettetDatoTomDato() {
        var aktuellOppgave = lagOppgave(LocalDate.now().minusDays(2));
        var uaktuellOppgave = lagOppgave(LocalDate.now());
        oppgaveRepository.lagre(uaktuellOppgave);
        oppgaveRepository.lagre(aktuellOppgave);
        var filtrerTomDato = LocalDate.now().minusDays(1);
        var query = new Oppgavespørring(avdelingIdForDrammen(),
                KøSortering.OPPRETT_BEHANDLING,
                List.of(BehandlingType.FØRSTEGANGSSØKNAD),
                List.of(FagsakYtelseType.FORELDREPENGER),
                List.of(),
                List.of(),
                false, //erDynamiskPeriode
                null,
                filtrerTomDato,
                null,
                null);
        var oppgaveResultat = oppgaveRepository.hentOppgaver(query);
        assertThat(oppgaveResultat).containsExactly(aktuellOppgave);
    }

    @Test
    public void filtrerPåFørsteStønadsdag() {
        var oppgave1 = basicOppgaveBuilder()
                .medFørsteStønadsdag(LocalDate.now().minusDays(1))
                .build();
        var oppgave2 = basicOppgaveBuilder()
                .medFørsteStønadsdag(LocalDate.now())
                .build();
        var oppgave3 = basicOppgaveBuilder()
                .medFørsteStønadsdag(LocalDate.now().plusDays(5))
                .build();
        oppgaveRepository.lagre(oppgave1);
        oppgaveRepository.lagre(oppgave2);
        oppgaveRepository.lagre(oppgave3);
        assertThat(filterOppgaver(oppgave1.getFørsteStønadsdag(), oppgave3.getFørsteStønadsdag()))
                .containsExactlyInAnyOrder(oppgave2, oppgave1, oppgave3);
        assertThat(filterOppgaver(oppgave1.getFørsteStønadsdag(), oppgave1.getFørsteStønadsdag())).containsExactly(oppgave1);
        assertThat(filterOppgaver(oppgave1.getFørsteStønadsdag().minusDays(10), oppgave1.getFørsteStønadsdag().minusDays(1))).isEmpty();
    }

    private List<Oppgave> filterOppgaver(LocalDate filtrerFomDato, LocalDate filtrerTomDato) {
        var query = new Oppgavespørring(avdelingIdForDrammen(),
                KøSortering.FØRSTE_STØNADSDAG,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                false,
                filtrerFomDato,
                filtrerTomDato,
                null,
                null);
        return oppgaveRepository.hentOppgaver(query);
    }

    @Test
    public void nullSistVedFeilutbetalingStartSomSorteringsKriterium() {
        // dersom oppdrag ikke leverer grunnlag innen frist, gir fptilbake opp og
        // lager hendelse som fører til oppgave. Formålet er at saksbehandler skal avklare
        // status på grunnlaget. Funksjonelt kan det variere om filtrene som brukes i enhetene
        // fanger opp disse (fom/tom på feltet vil ekskludere bla).
        var oppgaveUtenStartDato = tilbakekrevingOppgaveBuilder()
                .medBehandlingOpprettet(LocalDateTime.now().minusDays(2L))
                .medBehandlingId(behandlingId1)
                .medBeløp(BigDecimal.valueOf(0L))
                .medFeilutbetalingStart(null)
                .build();
        var oppgaveMedStartDato = tilbakekrevingOppgaveBuilder()
                .medBehandlingId(behandlingId2)
                .medBehandlingOpprettet(LocalDateTime.now().minusDays(1L))
                .medBeløp(BigDecimal.valueOf(10L))
                .medFeilutbetalingStart(LocalDateTime.now())
                .build();
        oppgaveRepository.lagre(oppgaveUtenStartDato);
        oppgaveRepository.lagre(oppgaveMedStartDato);

        var query = new Oppgavespørring(avdelingIdForDrammen(),
                FEILUTBETALINGSTART,
                List.of(),
                List.of(),
                List.of(), // inkluderes
                List.of(), //ekskluderes
                false,
                null,
                null,
                null,
                null);
        var oppgaver = oppgaveRepository.hentOppgaver(query);
        assertThat(oppgaver).containsExactly(oppgaveMedStartDato, oppgaveUtenStartDato);
    }

    @Test
    public void fårTomtSvarFraOppgaveFiltrering() {
        var filtrering = oppgaveRepository.hentOppgaveFilterSett(0L);
        assertThat(filtrering).isEmpty();
    }


    private Oppgave første() {
        return DBTestUtil.hentAlle(entityManager, Oppgave.class).get(0);
    }

    private Oppgave siste() {
        return DBTestUtil.hentAlle(entityManager, Oppgave.class).get(1);
    }

    private Oppgave lagOppgave(LocalDate opprettetDato) {
        return basicOppgaveBuilder(opprettetDato)
                .build();
    }

    private Oppgave.Builder basicOppgaveBuilder() {
        return basicOppgaveBuilder(LocalDate.now());
    }

    private Oppgave.Builder basicOppgaveBuilder(LocalDate opprettetDato) {
        return Oppgave.builder()
                .medFagsakSaksnummer(1337L)
                .medBehandlingId(behandlingId1)
                .medAktorId(5000000L)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medAktiv(true)
                .medBehandlingsfrist(LocalDateTime.now())
                .medBehandlendeEnhet(AVDELING_DRAMMEN_ENHET)
                .medBehandlingOpprettet(opprettetDato.atStartOfDay());
    }

    private Oppgave lagOppgave(String behandlendeEnhet) {
        return Oppgave.builder()
                .medFagsakSaksnummer(1337L)
                .medBehandlingId(behandlingId1)
                .medAktorId(5000000L).medBehandlendeEnhet(behandlendeEnhet)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medAktiv(true)
                .medBehandlingsfrist(LocalDateTime.now())
                .build();
    }

    private TilbakekrevingOppgave.Builder tilbakekrevingOppgaveBuilder() {
        return TilbakekrevingOppgave.tbuilder()
                .medFagsakSaksnummer(42L)
                .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medSystem("FPTILBAKE")
                .medBehandlingType(BehandlingType.TILBAKEBETALING)
                .medAktiv(true)
                .medAktorId(1L).medBehandlendeEnhet(AVDELING_DRAMMEN_ENHET);
    }
}
