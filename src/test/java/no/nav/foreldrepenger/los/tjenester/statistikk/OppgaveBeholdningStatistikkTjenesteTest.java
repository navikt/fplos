package no.nav.foreldrepenger.los.tjenester.statistikk;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRepository;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class OppgaveBeholdningStatistikkTjenesteTest {

    private final Oppgave førstegangOppgave = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave førstegangOppgave2 = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave klageOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.KLAGE).build();
    private final Oppgave innsynOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.INNSYN).build();
    private final Oppgave annenAvdeling = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlendeEnhet("5555")
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave beslutterOppgave = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave beslutterOppgave2 = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave lukketOppgave = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medAktiv(false)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();

    private NøkkeltallRepository nøkkeltallRepository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        nøkkeltallRepository = new NøkkeltallRepository(entityManager);
        this.entityManager = entityManager;
    }

    private void leggInnEttSettMedOppgaver() {
        entityManager.persist(førstegangOppgave);
        entityManager.persist(førstegangOppgave2);
        entityManager.persist(klageOppgave);
        entityManager.persist(innsynOppgave);

        beslutterOppgave.leggTilOppgaveEgenskap(
            OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
        entityManager.persist(beslutterOppgave);

        beslutterOppgave2.leggTilOppgaveEgenskap(
            OppgaveEgenskap.builder().medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER).medSisteSaksbehandlerForTotrinn("IDENT").build());
        entityManager.persist(beslutterOppgave2);

        entityManager.persist(lukketOppgave);

        entityManager.flush();
    }

    @Test
    void hentAlleOppgaverForAvdelingTest() {
        leggInnEttSettMedOppgaver();
        var resultater = nøkkeltallRepository.hentAlleOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);

        assertThat(resultater).hasSize(4);
        assertThat(resultater.get(0).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(0).tilBehandling()).isFalse();
        assertThat(resultater.get(0).antall()).isEqualTo(2L);

        assertThat(resultater.get(1).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(1).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(1).tilBehandling()).isTrue();
        assertThat(resultater.get(1).antall()).isEqualTo(2L);

        assertThat(resultater.get(2).antall()).isEqualTo(1L);
        assertThat(resultater.get(3).antall()).isEqualTo(1L);
    }

    @Test
        //FIXME - denne testen feiler fra midnatt til kl 01:00
        /*
         * [ERROR] Failures:
         * [ERROR]   OppgaveBeholdningStatistikkTjenesteTest.hentAntallOppgaverForAvdelingPerDatoTest:118
         * Expected size: 3 but was: 0 in:
         * []
         */
    void hentAntallOppgaverForAvdelingPerDatoTest() {
        leggInnEttSettMedOppgaver();
        var resultater = nøkkeltallRepository.hentAlleOppgaverForAvdelingPerDato(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(3);
        assertThat(resultater.get(0).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(0).opprettetDato()).isEqualTo(LocalDate.now());
        assertThat(resultater.get(0).antall()).isEqualTo(4L);
        assertThat(resultater.get(1).antall()).isEqualTo(1L);
        assertThat(resultater.get(2).antall()).isEqualTo(1L);
    }

    @Test
        //FIXME - denne testen feiler fra midnatt til kl 01:00
        /*
         * [ERROR]   OppgaveBeholdningStatistikkTjenesteTest.hentAntallOppgaverForAvdelingPerDatoTest2:136
         * expected: 2022-04-05 (java.time.LocalDate)
         *  but was: 2022-04-04 (java.time.LocalDate)
         */
    void hentAntallOppgaverForAvdelingPerDatoTest2() {
        leggTilOppgave(førstegangOppgave, 27, 27);
        leggTilOppgave(førstegangOppgave2, 28, 28);//skal ikke komme i resultatssettet
        leggTilOppgave(annenAvdeling, 10, 4);//skal ikke komme i resultatssettet
        leggTilOppgave(klageOppgave, 10, 4);
        var resultater = nøkkeltallRepository.hentAlleOppgaverForAvdelingPerDato(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(8);
        assertThat(resultater.get(0).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).opprettetDato()).isEqualTo(LocalDate.now().minusDays(27));
    }

    @Test
    void hentOppgaverPerFørsteStønadsdag() {
        leggInnEttSettMedOppgaver();
        var resultater = nøkkeltallRepository.hentOppgaverPerFørsteStønadsdag(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(1);
        assertThat(resultater.get(0).førsteStønadsdag()).isEqualTo(LocalDate.now().plusMonths(1));
        assertThat(resultater.get(0).antall()).isEqualTo(4L);
    }

    @Test
    void hentOppgaverPerFørsteStønadsdagUke() {
        leggInnEttSettMedOppgaver();
        var idag = LocalDate.now();
        var idagPlusMnd = idag.plusMonths(1);
        var resultater = nøkkeltallRepository.hentOppgaverPerFørsteStønadsdagUke(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(1);
        assertThat(resultater.get(0).førsteStønadsdag()).isEqualTo(idagPlusMnd.with(DayOfWeek.MONDAY));
        assertThat(resultater.get(0).førsteStønadsdagTekst()).isEqualTo(Year.from(idagPlusMnd) + "-" + idagPlusMnd.get(ChronoField.ALIGNED_WEEK_OF_YEAR));
        assertThat(resultater.get(0).antall()).isEqualTo(4L);
    }

    @Test
    void hentOppgaverPerFørsteStønadsdagMåned() {
        leggInnEttSettMedOppgaver();
        var idag = LocalDate.now();
        var idagPlusMnd = idag.plusMonths(1);
        var resultater = nøkkeltallRepository.hentOppgaverPerFørsteStønadsdagMåned(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(1);
        assertThat(resultater.get(0).førsteStønadsdag()).isEqualTo(idagPlusMnd.withDayOfMonth(1));
        assertThat(resultater.get(0).førsteStønadsdagTekst()).isEqualTo(Year.from(idagPlusMnd) + "-" + String.format("%02d", idagPlusMnd.getMonthValue()));
        assertThat(resultater.get(0).antall()).isEqualTo(4L);
    }

    private void leggTilOppgave(Oppgave oppgave, int startTilbakeITid, int sluttTilbakeITid) {
        entityManager.persist(oppgave);
        entityManager.flush();

        var now = LocalDateTime.now();
        var opprettetTid = now.minusDays(startTilbakeITid);
        var endretTid = now.minusDays(sluttTilbakeITid);

        entityManager.createQuery("""
        UPDATE Oppgave o
           SET o.opprettetTidspunkt = :opprettetTid,
               o.endretTidspunkt    = :endretTid,
               o.aktiv        = 'N'
         WHERE o.id = :oppgaveId
        """)
            .setParameter("opprettetTid", opprettetTid)
            .setParameter("endretTid", endretTid)
            .setParameter("oppgaveId", oppgave.getId())
            .executeUpdate();
    }
}
