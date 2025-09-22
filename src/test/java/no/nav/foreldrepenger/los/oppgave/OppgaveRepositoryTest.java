package no.nav.foreldrepenger.los.oppgave;

import static no.nav.foreldrepenger.los.DBTestUtil.avdelingDrammen;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.BEHANDLINGSFRIST;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.FEILUTBETALINGSTART;
import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;

@ExtendWith(JpaExtension.class)
class OppgaveRepositoryTest {

    private static final BehandlingId behandlingId1 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_1".getBytes()));
    private static final BehandlingId behandlingId2 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_2".getBytes()));
    private static final BehandlingId behandlingId3 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_3".getBytes()));
    private static final BehandlingId behandlingId4 = new BehandlingId(UUID.nameUUIDFromBytes("uuid_4".getBytes()));

    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveTjeneste oppgaveTjeneste;


    @BeforeEach
    void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, mock(ReservasjonTjeneste.class));
    }

    @Test
    void testHentingAvOppgaver() {
        lagStandardSettMedOppgaver();
        var alleOppgaverSpørring = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null, null, null);

        var oppgaves = oppgaveRepository.hentOppgaver(alleOppgaverSpørring);
        assertThat(oppgaves).hasSize(4);
        assertThat(oppgaveRepository.hentAntallOppgaver(alleOppgaverSpørring)).isEqualTo(4);
        assertThat(oppgaves).first().hasFieldOrPropertyWithValue("behandlendeEnhet", AVDELING_DRAMMEN_ENHET);
    }

    @Test
    void testHentingAvEventerVedBehandlingId() {
        lagStandardSettMedOppgaver();
        var event = oppgaveRepository.hentOppgaveEventer(behandlingId1).get(0);
        assertThat(event.getBehandlingId()).isEqualTo(behandlingId1);
    }

    private Saksnummer setupOppgaveMedEgenskaper(AndreKriterierType... kriterier) {
        var saksnummer = new Saksnummer(String.valueOf (Math.abs(new Random().nextLong() % 999999999)));
        var oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medSaksnummer(saksnummer).build();
        entityManager.persist(oppgave);
        for (var kriterie : kriterier) {
            var oppgaveEgenskapBuilder = OppgaveEgenskap.builder().medOppgave(oppgave).medAndreKriterierType(kriterie);
            if (kriterie.erTilBeslutter()) {
                oppgaveEgenskapBuilder.medSisteSaksbehandlerForTotrinn("IDENT");
            }
            entityManager.persist(oppgaveEgenskapBuilder.build());
        }
        entityManager.flush();
        return saksnummer;
    }

    @Test
    void testLagringAvOppgaveEgenskaper() {
        var saksnummerOppgaveEn = setupOppgaveMedEgenskaper(AndreKriterierType.UTLANDSSAK, AndreKriterierType.PAPIRSØKNAD);
        var lagredeOppgaver = oppgaveRepository.hentAktiveOppgaverForSaksnummer(List.of(saksnummerOppgaveEn));
        var lagredeEgenskaper = oppgaveRepository.hentOppgaveEgenskaper(lagredeOppgaver.get(0).getId());
        var lagredeKriterier = lagredeEgenskaper.stream().map(OppgaveEgenskap::getAndreKriterierType).collect(Collectors.toList());

        assertThat(lagredeKriterier).containsExactlyInAnyOrder(AndreKriterierType.UTLANDSSAK, AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    void testOppgaveSpørringMedEgenskaperfiltrering() {
        var saksnummerHit = setupOppgaveMedEgenskaper(AndreKriterierType.UTLANDSSAK, AndreKriterierType.UTBETALING_TIL_BRUKER);
        var oppgaveQuery = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, List.of(), List.of(), List.of(AndreKriterierType.UTLANDSSAK),
            // inkluderes
            List.of(AndreKriterierType.VURDER_SYKDOM), // ekskluderes
            false, null, null, null, null);
        var oppgaver = oppgaveRepository.hentOppgaver(oppgaveQuery);
        assertThat(oppgaver).hasSize(1);
        assertThat(oppgaver.getFirst().getSaksnummer()).isEqualTo(saksnummerHit);
    }

    @Test
    void testEkskluderingOgInkluderingAvOppgaver() {
        lagStandardSettMedOppgaver();
        var oppgaver = oppgaveRepository.hentOppgaver(new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(),
            List.of(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD), new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), List.of(AndreKriterierType.TIL_BESLUTTER),
                new ArrayList<>(), false, null, null, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                List.of(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD), // ekskluder andreKriterierType
                false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);

        oppgaver = oppgaveRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                List.of(AndreKriterierType.TIL_BESLUTTER),  // ekskluderAndreKriterierType
                false, null, null, null, null));
        assertThat(oppgaver).hasSize(2);

        oppgaver = oppgaveRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), List.of(AndreKriterierType.PAPIRSØKNAD),
                List.of(AndreKriterierType.TIL_BESLUTTER), false, null, null, null, null));
        assertThat(oppgaver).hasSize(1);
        var antallOppgaver = oppgaveRepository.hentAntallOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), List.of(AndreKriterierType.PAPIRSØKNAD),
                List.of(AndreKriterierType.TIL_BESLUTTER), false, null, null, null, null));
        assertThat(antallOppgaver).isEqualTo(1);

        var antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverForAvdeling).isEqualTo(4);

    }

    @Test
    void testAntallOppgaverForAvdeling() {
        var antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverForAvdeling).isZero();
        lagStandardSettMedOppgaver();
        antallOppgaverForAvdeling = oppgaveRepository.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverForAvdeling).isEqualTo(4);
    }

    @Test
    void testFiltreringDynamiskAvOppgaverIntervall() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                true, null, null, 1L, 10L));
        assertThat(oppgaves).hasSize(2);
    }

    @Test
    void testFiltreringDynamiskAvOppgaverBareFomDato() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                true, null, null, 15L, null));
        assertThat(oppgaves).hasSize(1);
    }

    @Test
    void testFiltreringDynamiskAvOppgaverBareTomDato() {
        lagStandardSettMedOppgaver();
        var oppgaves = oppgaveRepository.hentOppgaver(
            new Oppgavespørring(AVDELING_DRAMMEN_ENHET, BEHANDLINGSFRIST, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                true, null, null, null, 15L));
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
        var andreOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(behandlingId2)
            .medSaksnummer(new Saksnummer("222"))
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(9))
            .medBehandlingsfrist(LocalDateTime.now().plusDays(5))
            .build();
        var tredjeOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(behandlingId3)
            .medSaksnummer(new Saksnummer("333"))
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(8))
            .medBehandlingsfrist(LocalDateTime.now().plusDays(15))
            .build();
        var fjerdeOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingId(behandlingId4)
            .medSaksnummer(new Saksnummer("444"))
            .medBehandlingOpprettet(LocalDateTime.now())
            .medBehandlingsfrist(LocalDateTime.now())
            .build();
        entityManager.persist(førsteOppgave);
        entityManager.persist(andreOppgave);
        entityManager.persist(tredjeOppgave);
        entityManager.persist(fjerdeOppgave);
        entityManager.persist(OppgaveEgenskap.builder().medOppgave(førsteOppgave).medAndreKriterierType(AndreKriterierType.PAPIRSØKNAD).build());
        entityManager.persist(OppgaveEgenskap.builder()
            .medOppgave(andreOppgave)
            .medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER)
            .medSisteSaksbehandlerForTotrinn("IDENT")
            .build());
        entityManager.persist(OppgaveEgenskap.builder().medOppgave(tredjeOppgave).medAndreKriterierType(AndreKriterierType.PAPIRSØKNAD).build());
        entityManager.persist(OppgaveEgenskap.builder()
            .medOppgave(førsteOppgave)
            .medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER)
            .medSisteSaksbehandlerForTotrinn("IDENT")
            .build());
        entityManager.persist(
            new OppgaveEventLogg(behandlingId1, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
            new OppgaveEventLogg(behandlingId2, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
            new OppgaveEventLogg(behandlingId3, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
            new OppgaveEventLogg(behandlingId3, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, AVDELING_DRAMMEN_ENHET));
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
        var førsteOppgaveFiltrering = OppgaveFiltrering.builder()
            .medNavn("OPPRETTET")
            .medSortering(KøSortering.OPPRETT_BEHANDLING)
            .medAvdeling(avdeling)
            .build();
        var andreOppgaveFiltrering = OppgaveFiltrering.builder()
            .medNavn("BEHANDLINGSFRIST")
            .medSortering(BEHANDLINGSFRIST)
            .medAvdeling(avdeling)
            .build();

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
        var oppgaveKommerPåNytt = lagOppgave(AVDELING_DRAMMEN_ENHET);
        persistFlush(oppgave);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(1);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(oppgave.getBehandlingId());
        persistFlush(oppgaveKommerPåNytt);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(2);
    }

    @Test
    void skalKasteExceptionVedLukkingAvOppgaveDerDetFinnesFlereAktiveOppgaver() {
        var første = lagOppgave(AVDELING_DRAMMEN_ENHET);
        persistFlush(første);
        var siste = lagOppgave(AVDELING_DRAMMEN_ENHET);
        persistFlush(siste);
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(2);
        assertThat(første()).isEqualTo(første);
        assertThat(siste().getAktiv()).isTrue();
        assertThat(første().getOpprettetTidspunkt()).isBefore(siste().getOpprettetTidspunkt());
        var behandlingId = første.getBehandlingId();
        assertThrows(IllegalStateException.class, () -> oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId));
    }

    @Test
    void filtrerPåOpprettetDatoTomDato() {
        var aktuellOppgave = lagOppgave(LocalDate.now().minusDays(2));
        var uaktuellOppgave = lagOppgave(LocalDate.now());
        oppgaveRepository.lagre(uaktuellOppgave);
        oppgaveRepository.lagre(aktuellOppgave);
        var filtrerTomDato = LocalDate.now().minusDays(1);
        var query = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.OPPRETT_BEHANDLING, List.of(BehandlingType.FØRSTEGANGSSØKNAD),
            List.of(FagsakYtelseType.FORELDREPENGER), List.of(), List.of(), false,
            null, filtrerTomDato, null, null);
        var oppgaveResultat = oppgaveRepository.hentOppgaver(query);
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
        var oppgave1 = tilbakekrevingOppgaveBuilder().medBehandlingOpprettet(LocalDateTime.now().minusDays(2L))
            .medBehandlingId(behandlingId1)
            .medBeløp(BigDecimal.valueOf(100L))
            .build();
        var oppgave2 = tilbakekrevingOppgaveBuilder().medBehandlingId(behandlingId2)
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(1L))
            .medBeløp(BigDecimal.valueOf(200L))
            .build();
        oppgaveRepository.lagre(oppgave1);
        oppgaveRepository.lagre(oppgave2);

        var queryFiltrertPåBeløpsstørrelse = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BELØP, List.of(), List.of(), List.of(),
            List.of(), false, null, null, 50L, 150L);
        var oppgaver = oppgaveRepository.hentOppgaver(queryFiltrertPåBeløpsstørrelse);
        assertThat(oppgaver).containsExactly(oppgave1);

        var querySortertPåBeløpsstørrelseDesc = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BELØP, List.of(), List.of(), List.of(),
            List.of(), false, null, null, null, null);
        var oppgaverSortert = oppgaveRepository.hentOppgaver(querySortertPåBeløpsstørrelseDesc);
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
            .medBeløp(BigDecimal.valueOf(0L))
            .medFeilutbetalingStart(null)
            .build();
        var oppgaveMedStartDato = tilbakekrevingOppgaveBuilder().medBehandlingId(behandlingId2)
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(1L))
            .medBeløp(BigDecimal.valueOf(10L))
            .medFeilutbetalingStart(LocalDateTime.now())
            .build();
        oppgaveRepository.lagre(oppgaveUtenStartDato);
        oppgaveRepository.lagre(oppgaveMedStartDato);

        var query = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, FEILUTBETALINGSTART, List.of(), List.of(), List.of(),
            List.of(), false, null, null, null, null);
        var oppgaver = oppgaveRepository.hentOppgaver(query);
        assertThat(oppgaver).containsExactly(oppgaveMedStartDato, oppgaveUtenStartDato);
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
        var oeBuilder = OppgaveEgenskap.builder().medOppgave(oppgave).medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn(
            BrukerIdent.brukerIdent());
        entityManager.persist(oppgave);
        entityManager.persist(oeBuilder.build());
        entityManager.flush();

        // saksbehandlere bør ikke få opp et antall som ikke stemmer med det de ser i køen (egne vedtak til beslutter filtreres bort fra beslutterkø)
        var beslutterKøIkkeAvdelingsleder = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, List.of(),
            List.of(), List.of(AndreKriterierType.TIL_BESLUTTER), List.of(), false, null, null, null, null);
        beslutterKøIkkeAvdelingsleder.setForAvdelingsleder(false);
        var oppgaver = oppgaveRepository.hentAntallOppgaver(beslutterKøIkkeAvdelingsleder);
        assertThat(oppgaver).isZero();

        // avdelingsleder skal se antallet i avdelingslederkontekst, også eventuelle egne foreslåtte vedtak der avdelingsleder også er saksbehandler
        var beslutterKøAvdelingsleder = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.BEHANDLINGSFRIST, List.of(),
            List.of(), List.of(AndreKriterierType.TIL_BESLUTTER), List.of(), false, null, null, null, null);
        beslutterKøAvdelingsleder.setForAvdelingsleder(true);
        var oppgaveAntallAdelingsleder = oppgaveRepository.hentAntallOppgaver(beslutterKøAvdelingsleder);
        assertThat(oppgaveAntallAdelingsleder).isEqualTo(1);
    }

    private List<Oppgave> filterOppgaver(LocalDate filtrerFomDato, LocalDate filtrerTomDato) {
        var query = new Oppgavespørring(AVDELING_DRAMMEN_ENHET, KøSortering.FØRSTE_STØNADSDAG, List.of(), List.of(), List.of(), List.of(), false,
            filtrerFomDato, filtrerTomDato, null, null);
        return oppgaveRepository.hentOppgaver(query);
    }

    private Oppgave første() {
        return DBTestUtil.hentAlle(entityManager, Oppgave.class).get(0);
    }

    private Oppgave siste() {
        return DBTestUtil.hentAlle(entityManager, Oppgave.class).get(1);
    }

    private Oppgave lagOppgave(LocalDate opprettetDato) {
        return basicOppgaveBuilder(opprettetDato).build();
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

    private TilbakekrevingOppgave.Builder tilbakekrevingOppgaveBuilder() {
        return TilbakekrevingOppgave.tbuilder()
            .medSaksnummer(new Saksnummer("42"))
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medSystem("FPTILBAKE")
            .medBehandlingType(BehandlingType.TILBAKEBETALING)
            .medAktiv(true)
            .medAktorId(AktørId.dummy())
            .medBehandlendeEnhet(AVDELING_DRAMMEN_ENHET);
    }

    private void persistFlush(Oppgave oppgave) {
        entityManager.persist(oppgave);
        entityManager.flush();
    }
}
