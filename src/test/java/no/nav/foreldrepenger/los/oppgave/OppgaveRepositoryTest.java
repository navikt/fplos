package no.nav.foreldrepenger.los.oppgave;

import static no.nav.foreldrepenger.los.DBTestUtil.avdelingDrammen;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.BEHANDLINGSFRIST;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.FEILUTBETALINGSTART;
import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;

@ExtendWith(JpaExtension.class)
class OppgaveRepositoryTest {

    private static final BehandlingId behandlingId1 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_1".getBytes()));
    private static final BehandlingId behandlingId2 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_2".getBytes()));
    private static final BehandlingId behandlingId3 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_3".getBytes()));
    private static final BehandlingId behandlingId4 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_4".getBytes()));

    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveKøRepository oppgaveKøRepository;


    @BeforeEach
    void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        oppgaveKøRepository = new OppgaveKøRepository(entityManager);
    }

    @Test
    void testHentingAvOppgaver() {
        lagStandardSettMedOppgaver();
        var alleOppgaverSpørring = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Periodefilter.FAST_PERIODE, null, null, null, null,
            Filtreringstype.ALLE, null, null);

        var oppgaves = oppgaveKøRepository.hentOppgaver(alleOppgaverSpørring);
        assertThat(oppgaves).hasSize(5);
        assertThat(oppgaveKøRepository.hentAntallOppgaver(alleOppgaverSpørring)).isEqualTo(5);
        assertThat(oppgaves).first().hasFieldOrPropertyWithValue("behandlendeEnhet", AVDELING_DRAMMEN_ENHET);
    }
    private Saksnummer setupOppgaveMedEgenskaper(AndreKriterierType... kriterier) {
        var saksnummer = new Saksnummer(String.valueOf (Math.abs(new Random().nextLong() % 999999999)));
        var oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medSaksnummer(saksnummer).build();
        for (var kriterie : kriterier) {
            var oppgaveEgenskapBuilder = OppgaveEgenskap.builder().medAndreKriterierType(kriterie);
            if (kriterie.erTilBeslutter()) {
                oppgaveEgenskapBuilder.medSisteSaksbehandlerForTotrinn("IDENT");
            }
            oppgave.leggTilOppgaveEgenskap(oppgaveEgenskapBuilder.build());
        }
        entityManager.persist(oppgave);
        entityManager.flush();
        return saksnummer;
    }

    @Test
    void testOppgaveSpørringMedEgenskaperfiltrering() {
        var saksnummerHit = setupOppgaveMedEgenskaper(AndreKriterierType.UTLANDSSAK, AndreKriterierType.UTBETALING_TIL_BRUKER);
        var oppgaveQuery = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, List.of(), List.of(), List.of(AndreKriterierType.UTLANDSSAK),
            // inkluderes
            List.of(AndreKriterierType.VURDER_SYKDOM), // ekskluderes
            Periodefilter.FAST_PERIODE, null, null, null, null, Filtreringstype.ALLE, null, null);
        var oppgaver = oppgaveKøRepository.hentOppgaver(oppgaveQuery);
        assertThat(oppgaver).hasSize(1);
        assertThat(oppgaver.getFirst().getSaksnummer()).isEqualTo(saksnummerHit);
    }

    @Test
    void testEkskluderingOgInkluderingAvOppgaver() {
        lagStandardSettMedOppgaver();
        var oppgaver = oppgaveKøRepository.hentOppgaver(new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(),
            List.of(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD), new ArrayList<>(), Periodefilter.FAST_PERIODE, null, null, null, null,
            Filtreringstype.ALLE, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveKøRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), List.of(AndreKriterierType.TIL_BESLUTTER),
                new ArrayList<>(), Periodefilter.FAST_PERIODE, null, null, null, null, Filtreringstype.ALLE, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveKøRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                List.of(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD), // ekskluder andreKriterierType
                Periodefilter.FAST_PERIODE, null, null, null, null, Filtreringstype.ALLE, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveKøRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                List.of(AndreKriterierType.TIL_BESLUTTER),  // ekskluderAndreKriterierType
                Periodefilter.FAST_PERIODE, null, null, null, null, Filtreringstype.ALLE, null, null));
        assertThat(oppgaver).hasSize(3);

        oppgaver = oppgaveKøRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), List.of(AndreKriterierType.PAPIRSØKNAD),
                List.of(AndreKriterierType.TIL_BESLUTTER), Periodefilter.FAST_PERIODE, null, null, null, null, Filtreringstype.ALLE, null, null));
        assertThat(oppgaver).hasSize(1);
        var antallOppgaver = oppgaveKøRepository.hentAntallOppgaver(
                new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), List.of(AndreKriterierType.PAPIRSØKNAD),
                List.of(AndreKriterierType.TIL_BESLUTTER), Periodefilter.FAST_PERIODE, null, null, null, null,
                Filtreringstype.LEDIGE, null, null));
        assertThat(antallOppgaver).isEqualTo(1);

        var antallOppgaverForAvdeling = oppgaveKøRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverForAvdeling).isEqualTo(5);

    }

    @Test
    void testAntallOppgaverForAvdeling() {
        var antallOppgaverForAvdeling = oppgaveKøRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverForAvdeling).isZero();
        lagStandardSettMedOppgaver();
        antallOppgaverForAvdeling = oppgaveKøRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverForAvdeling).isEqualTo(5);
    }

    @Test
    void testFiltreringRelativAvOppgaverIntervall() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveKøRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                Periodefilter.RELATIV_PERIODE_DAGER, null, null, 1L, 10L, Filtreringstype.ALLE, null, null));
        assertThat(oppgaves).hasSize(2);
    }

    @Test
    void testFiltreringRelativAvOppgaverBareFomDato() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveKøRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                Periodefilter.RELATIV_PERIODE_DAGER, null, null, 15L, null, Filtreringstype.ALLE, null, null));
        assertThat(oppgaves).hasSize(2);
    }

    @Test
    void testFiltreringRelativAvOppgaverBareTomDato() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveKøRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                Periodefilter.RELATIV_PERIODE_DAGER, null, null, null, 15L, Filtreringstype.ALLE, null, null));
        assertThat(oppgaves).hasSize(4);
    }

    @Test
    void testFiltreringRelativAvOppgaverIntervallMåneder() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveKøRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                Periodefilter.RELATIV_PERIODE_MÅNEDER, null, null, -3L, 5L, Filtreringstype.ALLE, null, null));
        assertThat(oppgaves).hasSize(4);
    }

    private void lagStandardSettMedOppgaver() {
        var førsteOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(behandlingId1)
            .medSaksnummer(new Saksnummer("111"))
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(10))
            .medBehandlingsfrist(LocalDateTime.now().plusDays(10))
            .build();
        førsteOppgave.leggTilOppgaveEgenskap(OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.PAPIRSØKNAD).build());
        førsteOppgave.leggTilOppgaveEgenskap(OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
        var andreOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(behandlingId2)
            .medSaksnummer(new Saksnummer("222"))
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(9))
            .medBehandlingsfrist(LocalDateTime.now().plusDays(5))
            .build();
        andreOppgave.leggTilOppgaveEgenskap(
            OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
        var tredjeOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(behandlingId3)
            .medSaksnummer(new Saksnummer("333"))
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(8))
            .medBehandlingsfrist(LocalDateTime.now().plusDays(15))
            .build();
        tredjeOppgave.leggTilOppgaveEgenskap(OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.PAPIRSØKNAD).build());
        var fjerdeOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(behandlingId4)
            .medSaksnummer(new Saksnummer("444"))
            .medBehandlingOpprettet(LocalDateTime.now())
            .medBehandlingsfrist(LocalDateTime.now())
            .build();

        var femteOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(behandlingId4)
            .medSaksnummer(new Saksnummer("555"))
            .medBehandlingOpprettet(LocalDateTime.now())
            .medBehandlingsfrist(LocalDateTime.now().plusYears(1))
            .build();

        entityManager.persist(førsteOppgave);
        entityManager.persist(andreOppgave);
        entityManager.persist(tredjeOppgave);
        entityManager.persist(fjerdeOppgave);
        entityManager.persist(femteOppgave);

        entityManager.flush();
    }

    @Test
    void testReservering() {
        var oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingOpprettet(LocalDateTime.now().minusDays(10)).build();
        entityManager.persist(oppgave);
        entityManager.flush();
        var reservertOppgave = oppgaveRepository.hentReservasjon(oppgave.getId());
        assertThat(reservertOppgave).isNotNull();
    }

    @Test
    void hentAlleLister() {
        var avdeling = avdelingDrammen(entityManager);
        var førsteOppgaveFiltrering = new OppgaveFiltrering();
        førsteOppgaveFiltrering.setNavn("OPPRETTET");
        førsteOppgaveFiltrering.setSortering(KøSortering.OPPRETT_BEHANDLING);
        førsteOppgaveFiltrering.setAvdeling(avdeling);
        var andreOppgaveFiltrering = new OppgaveFiltrering();
        andreOppgaveFiltrering.setNavn("BEHANDLINGSFRIST");
        andreOppgaveFiltrering.setSortering(BEHANDLINGSFRIST);
        andreOppgaveFiltrering.setAvdeling(avdeling);

        entityManager.persist(førsteOppgaveFiltrering);
        entityManager.persist(andreOppgaveFiltrering);
        entityManager.flush();

        var lister = oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetEnhet(Avdeling.AVDELING_DRAMMEN_ENHET);

        assertThat(lister).extracting(OppgaveFiltrering::getNavn).contains("OPPRETTET", "BEHANDLINGSFRIST");
        assertThat(lister).extracting(OppgaveFiltrering::getAvdeling).contains(avdeling);
        assertThat(lister).extracting(OppgaveFiltrering::getSortering).contains(BEHANDLINGSFRIST, KøSortering.OPPRETT_BEHANDLING);
    }

    @Test
    void lagreOppgaveHvisForskjelligEnhet() {
        var oppgave = lagOppgave(AVDELING_DRAMMEN_ENHET);
        var AVDELING_ANNET_ENHET = "4000";
        var oppgaveKommerPåNytt = lagOppgave(AVDELING_ANNET_ENHET);
        persistFlush(oppgave);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(1);
        persistFlush(oppgaveKommerPåNytt);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(2);
    }

    @Test
    void lagreOppgaveHvisAvsluttetFraFør() {
        var oppgave = lagOppgave(AVDELING_DRAMMEN_ENHET);
        persistFlush(oppgave);
        var oppgaveKommerPåNytt = lagOppgave(AVDELING_DRAMMEN_ENHET);
        persistFlush(oppgaveKommerPåNytt);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(2);
    }

    @Test
    void filtrerPåOpprettetDatoTomDato() {
        var aktuellOppgave = basicOppgaveBuilder(LocalDate.now().minusDays(2)).build();
        var uaktuellOppgave = basicOppgaveBuilder(LocalDate.now()).build();
        oppgaveRepository.lagre(uaktuellOppgave);
        oppgaveRepository.lagre(aktuellOppgave);
        var filtrerTomDato = LocalDate.now().minusDays(1);
        var query = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.OPPRETT_BEHANDLING, List.of(BehandlingType.FØRSTEGANGSSØKNAD),
            List.of(FagsakYtelseType.FORELDREPENGER), List.of(), List.of(), Periodefilter.FAST_PERIODE,
            null, filtrerTomDato, null, null, Filtreringstype.ALLE, null, null);
        var oppgaveResultat = oppgaveKøRepository.hentOppgaver(query);
        assertThat(oppgaveResultat).containsExactly(aktuellOppgave);
    }

    @Test
    void filtrerPåFørsteStønadsdag() {
        var oppgave1 = basicOppgaveBuilder().medFørsteStønadsdag(LocalDate.now().minusDays(1)).build();
        var oppgave2 = basicOppgaveBuilder().medFørsteStønadsdag(LocalDate.now()).build();
        var oppgave3 = basicOppgaveBuilder().medFørsteStønadsdag(LocalDate.now().plusDays(5)).build();
        oppgaveRepository.lagre(oppgave1);
        oppgaveRepository.lagre(oppgave2);
        oppgaveRepository.lagre(oppgave3);
        Assertions.assertThat(filterOppgaver(oppgave1.getFørsteStønadsdag(), oppgave3.getFørsteStønadsdag())).containsExactlyInAnyOrder(oppgave2, oppgave1,
            oppgave3);
        Assertions.assertThat(filterOppgaver(oppgave1.getFørsteStønadsdag(), oppgave1.getFørsteStønadsdag())).containsExactly(oppgave1);
        Assertions.assertThat(filterOppgaver(oppgave1.getFørsteStønadsdag().minusDays(10), oppgave1.getFørsteStønadsdag().minusDays(1))).isEmpty();
    }

    @Test
    void filtrerSorterFeilutbetaltBeløp() {
        var oppgave1 = tilbakekrevingOppgaveBuilder()
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(2L))
            .medBehandlingId(behandlingId1)
            .medFeilutbetalingBeløp(BigDecimal.valueOf(100L))
            .build();
        var oppgave2 = tilbakekrevingOppgaveBuilder().medBehandlingId(behandlingId2)
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(1L))
            .medFeilutbetalingBeløp(BigDecimal.valueOf(200L))
            .build();
        oppgaveRepository.lagre(oppgave1);
        oppgaveRepository.lagre(oppgave2);

        var queryFiltrertPåBeløpsstørrelse = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BELØP, List.of(), List.of(), List.of(),
            List.of(), Periodefilter.FAST_PERIODE, null, null, 50L, 150L, Filtreringstype.ALLE, null, null);
        var oppgaver = oppgaveKøRepository.hentOppgaver(queryFiltrertPåBeløpsstørrelse);
        assertThat(oppgaver).containsExactly(oppgave1);

        var querySortertPåBeløpsstørrelseDesc = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BELØP, List.of(), List.of(), List.of(),
            List.of(), Periodefilter.FAST_PERIODE, null, null, null, null, Filtreringstype.ALLE, null, null);
        var oppgaverSortert = oppgaveKøRepository.hentOppgaver(querySortertPåBeløpsstørrelseDesc);
        assertThat(oppgaverSortert).containsExactly(oppgave2, oppgave1);
    }

    @Test
    void nullSistVedFeilutbetalingStartSomSorteringsKriterium() {
        // dersom oppdrag ikke leverer grunnlag innen frist, gir fptilbake opp og
        // lager hendelse som fører til oppgave. Formålet er at saksbehandler skal avklare
        // status på grunnlaget. Funksjonelt kan det variere om filtrene som brukes i enhetene
        // fanger opp disse (fom/tom på feltet vil ekskludere bla).
        var oppgaveUtenStartDato = tilbakekrevingOppgaveBuilder().medBehandlingOpprettet(LocalDateTime.now().minusDays(2L))
            .medBehandlingId(behandlingId1)
            .medFeilutbetalingBeløp(BigDecimal.valueOf(0L))
            .medFeilutbetalingStart(null)
            .build();
        var oppgaveMedStartDato = tilbakekrevingOppgaveBuilder().medBehandlingId(behandlingId2)
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(1L))
            .medFeilutbetalingBeløp(BigDecimal.valueOf(10L))
            .medFeilutbetalingStart(LocalDateTime.now())
            .build();
        oppgaveRepository.lagre(oppgaveUtenStartDato);
        oppgaveRepository.lagre(oppgaveMedStartDato);

        var query = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, FEILUTBETALINGSTART, List.of(), List.of(), List.of(),
            List.of(), Periodefilter.FAST_PERIODE, null, null, null, null, Filtreringstype.ALLE, null, null);
        var oppgaver = oppgaveKøRepository.hentOppgaver(query);
        assertThat(oppgaver).containsExactly(oppgaveMedStartDato, oppgaveUtenStartDato);
    }

    @Test
    void skalKunneSorterePåFørsteStønadsdagSynkende() {
        var oppgave1 = basicOppgaveBuilder().medFørsteStønadsdag(LocalDate.now().minusDays(1)).build();
        var oppgave2 = basicOppgaveBuilder().medFørsteStønadsdag(LocalDate.now()).build();
        var oppgave3 = basicOppgaveBuilder().medFørsteStønadsdag(LocalDate.now().plusDays(5)).build();
        var oppgave4 = basicOppgaveBuilder().medFørsteStønadsdag(null).build(); // verifiserer nulls last
        oppgaveRepository.lagre(oppgave1);
        oppgaveRepository.lagre(oppgave2);
        oppgaveRepository.lagre(oppgave3);
        oppgaveRepository.lagre(oppgave4);

        var query = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.FØRSTE_STØNADSDAG_SYNKENDE, List.of(), List.of(), List.of(), List.of(), Periodefilter.FAST_PERIODE,
            null, null, null, null, Filtreringstype.ALLE, null, null);
        var oppgaver = oppgaveKøRepository.hentOppgaver(query);
        Assertions.assertThat(oppgaver).containsExactly(oppgave3, oppgave2, oppgave1, oppgave4);

        var queryAvgrenset = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.FØRSTE_STØNADSDAG_SYNKENDE, List.of(), List.of(), List.of(), List.of(), Periodefilter.FAST_PERIODE,
            oppgave2.getFørsteStønadsdag(), oppgave3.getFørsteStønadsdag(), null, null, Filtreringstype.ALLE, null, null);
        var oppgaverAvgrenset = oppgaveKøRepository.hentOppgaver(queryAvgrenset);
        Assertions.assertThat(oppgaverAvgrenset).containsExactly(oppgave3, oppgave2);
    }

    @Test
    void fårTomtSvarFraOppgaveFiltrering() {
        var filtrering = oppgaveRepository.hentOppgaveFilterSett(0L);
        assertThat(filtrering).isEmpty();
    }

    @Test
    void avdelingslederTellerMedEgneReservasjoner() {
        var saksnummer = new Saksnummer(String.valueOf (Math.abs(new Random().nextLong() % 999999999)));
        var oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medSaksnummer(saksnummer).build();
        oppgave.leggTilOppgaveEgenskap(OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn(
            BrukerIdent.brukerIdent()).build());
        entityManager.persist(oppgave);
        entityManager.flush();

        // saksbehandlere bør ikke få opp et antall som ikke stemmer med det de ser i køen (egne vedtak til beslutter filtreres bort fra beslutterkø)
        var beslutterKøIkkeAvdelingsleder = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, List.of(),
            List.of(), List.of(AndreKriterierType.TIL_BESLUTTER), List.of(), Periodefilter.FAST_PERIODE, null, null, null, null,
            Filtreringstype.LEDIGE, null, null);
        var oppgaver = oppgaveKøRepository.hentAntallOppgaver(beslutterKøIkkeAvdelingsleder);
        assertThat(oppgaver).isZero();

        // avdelingsleder skal se antallet i avdelingslederkontekst, også eventuelle egne foreslåtte vedtak der avdelingsleder også er saksbehandler
        var beslutterKøAvdelingsleder = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, List.of(),
            List.of(), List.of(AndreKriterierType.TIL_BESLUTTER), List.of(), Periodefilter.FAST_PERIODE, null, null, null, null,
            Filtreringstype.ALLE, null, null);
        var oppgaveAntallAdelingsleder = oppgaveKøRepository.hentAntallOppgaver(beslutterKøAvdelingsleder);
        assertThat(oppgaveAntallAdelingsleder).isEqualTo(1);
    }

    @Test
    void lagre_behandling_finn_den() {
        var behandling= Behandling.builder(Optional.empty())
            .medKildeSystem(Fagsystem.FPSAK)
            .dummyBehandling(AVDELING_DRAMMEN_ENHET, BehandlingTilstand.AKSJONSPUNKT)
            .build();
        oppgaveRepository.lagre(behandling);
        var hentet = oppgaveRepository.finnBehandling(behandling.getId());
        assertThat(hentet).isPresent();
        assertThat(hentet.get().getId()).isEqualTo(behandling.getId());
        assertThat(hentet.get().getBehandlingTilstand()).isEqualTo(BehandlingTilstand.AKSJONSPUNKT);
        var oppdatert = Behandling.builder(hentet)
            .medBehandlingTilstand(BehandlingTilstand.VENT_MANUELL)
            .build();
        oppgaveRepository.lagre(oppdatert);
        hentet = oppgaveRepository.finnBehandling(behandling.getId());
        assertThat(hentet).isPresent();
        assertThat(hentet.get().getBehandlingTilstand()).isEqualTo(BehandlingTilstand.VENT_MANUELL);
    }

    @Test
    void skalKunneSorterePåOppgaveOpprettetTidStigende() {
        Function<LocalDateTime, Oppgave> lagBeslutterOppgave = (LocalDateTime behandlingsfrist) -> {
            var oppgave = basicOppgaveBuilder().medBehandlingsfrist(behandlingsfrist).build();
            oppgave.leggTilOppgaveEgenskap(
                OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
            return oppgave;
        };

        var now = LocalDateTime.now();
        var t1 = now.minusHours(3);
        var t2   = now.minusHours(2);
        var t3   = now.minusHours(1);

        // setter omvendt tid på behandlingsfrist for å sørge for motsatt sortering med default sortering
        var beslutterOppgaveEldste = lagBeslutterOppgave.apply(t3);
        var beslutterOppgaveMellomste = lagBeslutterOppgave.apply(t2);
        var beslutterOppgaveNyeste = lagBeslutterOppgave.apply(t1);

        entityManager.persist(beslutterOppgaveEldste);
        entityManager.persist(beslutterOppgaveMellomste);
        entityManager.persist(beslutterOppgaveNyeste);
        entityManager.flush();

        // for enkel, pålitelig test må vi patche oppgave.opprettet_tid via nativequery
        setOpprettetTid(beslutterOppgaveEldste, t1);
        setOpprettetTid(beslutterOppgaveMellomste, t2);
        setOpprettetTid(beslutterOppgaveNyeste, t3);
        entityManager.flush();

        var oppgaveOpprettetSorteringQuery = new Oppgavespørring(
            AVDELING_DRAMMEN_ENHET,
            KøSortering.OPPGAVE_OPPRETTET,
            List.of(),
            List.of(),
            List.of(AndreKriterierType.TIL_BESLUTTER),
            List.of(),
            Periodefilter.FAST_PERIODE,
            null, null, null, null,
            Filtreringstype.ALLE,
            null, null
        );
        var oppgaver = oppgaveKøRepository.hentOppgaver(oppgaveOpprettetSorteringQuery);
        assertThat(oppgaver).hasSize(3);
        assertThat(oppgaver).extracting(Oppgave::getId)
            .containsExactly(
                beslutterOppgaveEldste.getId(),
                beslutterOppgaveMellomste.getId(),
                beslutterOppgaveNyeste.getId()
            );

        // sjekker at default sortering gir motsatt rekkefølge
        var defaultSorteringQuery = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Periodefilter.FAST_PERIODE, null, null, null, null,
            Filtreringstype.ALLE, null, null);
        var defaultSorteringOppgaver = oppgaveKøRepository.hentOppgaver(defaultSorteringQuery);
        assertThat(defaultSorteringOppgaver).hasSize(3);
        assertThat(defaultSorteringOppgaver).extracting(Oppgave::getId)
            .containsExactly(
                beslutterOppgaveNyeste.getId(),
                beslutterOppgaveMellomste.getId(),
                beslutterOppgaveEldste.getId()
            );
    }

    private void setOpprettetTid(Oppgave oppgave, LocalDateTime ts) {
        entityManager.createNativeQuery(
                "update OPPGAVE set OPPRETTET_TID = :ts where id = :id"
            )
            .setParameter("ts", ts)
            .setParameter("id", oppgave.getId())
            .executeUpdate();
    }

    private List<Oppgave> filterOppgaver(LocalDate filtrerFomDato, LocalDate filtrerTomDato) {
        var query = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.FØRSTE_STØNADSDAG, List.of(), List.of(), List.of(), List.of(), Periodefilter.FAST_PERIODE,
            filtrerFomDato, filtrerTomDato, null, null, Filtreringstype.ALLE, null, null);
        return oppgaveKøRepository.hentOppgaver(query);
    }

    private Oppgave.Builder basicOppgaveBuilder() {
        return basicOppgaveBuilder(LocalDate.now());
    }

    private Oppgave.Builder basicOppgaveBuilder(LocalDate opprettetDato) {
        return Oppgave.builder()
            .medSaksnummer(new Saksnummer("1337"))
            .medBehandlingId(behandlingId1)
            .medAktørId(AktørId.dummy())
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medAktiv(true)
            .medBehandlingsfrist(LocalDateTime.now())
            .medBehandlendeEnhet(AVDELING_DRAMMEN_ENHET)
            .medBehandlingOpprettet(opprettetDato.atStartOfDay());
    }

    private Oppgave lagOppgave(String behandlendeEnhet) {
        return Oppgave.builder()
            .medSaksnummer(new Saksnummer("1337"))
            .medBehandlingId(behandlingId1)
            .medAktørId(AktørId.dummy())
            .medBehandlendeEnhet(behandlendeEnhet)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medAktiv(true)
            .medBehandlingsfrist(LocalDateTime.now())
            .build();
    }

    private Oppgave.Builder tilbakekrevingOppgaveBuilder() {
        return Oppgave.builder()
            .medSaksnummer(new Saksnummer("42"))
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medSystem(Fagsystem.FPTILBAKE)
            .medBehandlingType(BehandlingType.TILBAKEBETALING)
            .medAktiv(true)
            .medAktørId(AktørId.dummy())
            .medBehandlendeEnhet(AVDELING_DRAMMEN_ENHET);
    }

    private void persistFlush(Oppgave oppgave) {
        entityManager.persist(oppgave);
        entityManager.flush();
    }


}
