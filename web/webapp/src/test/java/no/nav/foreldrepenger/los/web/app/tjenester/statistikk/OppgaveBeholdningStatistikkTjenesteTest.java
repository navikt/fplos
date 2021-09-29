package no.nav.foreldrepenger.los.web.app.tjenester.statistikk;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.StatistikkRepository;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaveBeholdningStatistikkTjeneste;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
@ExtendWith(MockitoExtension.class)
public class OppgaveBeholdningStatistikkTjenesteTest {

    private final Oppgave førstegangOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .build();
    private final Oppgave førstegangOppgave2 = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .build();
    private final Oppgave klageOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.KLAGE)
            .build();
    private final Oppgave innsynOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.INNSYN)
            .build();
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

    private OppgaveBeholdningStatistikkTjeneste oppgaveBeholdningStatistikkTjeneste;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        var statisikkRepository = new StatistikkRepository(entityManager);
        oppgaveBeholdningStatistikkTjeneste = new OppgaveBeholdningStatistikkTjeneste(statisikkRepository);
        this.entityManager = entityManager;
    }

    private void leggInnEttSettMedOppgaver() {
        entityManager.persist(førstegangOppgave);
        entityManager.persist(førstegangOppgave2);
        entityManager.persist(klageOppgave);
        entityManager.persist(innsynOppgave);

        entityManager.persist(beslutterOppgave);
        entityManager.persist(new OppgaveEgenskap(beslutterOppgave, AndreKriterierType.TIL_BESLUTTER));

        entityManager.persist(beslutterOppgave2);
        entityManager.persist(new OppgaveEgenskap(beslutterOppgave2, AndreKriterierType.TIL_BESLUTTER));

        entityManager.persist(lukketOppgave);

        entityManager.flush();
    }

    @Test
    public void hentAlleOppgaverForAvdelingTest() {
        leggInnEttSettMedOppgaver();
        var resultater = oppgaveBeholdningStatistikkTjeneste.hentAlleOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);

        assertThat(resultater).hasSize(4);
        assertThat(resultater.get(0).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(0).tilBehandling()).isEqualTo(false);
        assertThat(resultater.get(0).antall()).isEqualTo(2L);

        assertThat(resultater.get(1).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(1).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(1).tilBehandling()).isEqualTo(true);
        assertThat(resultater.get(1).antall()).isEqualTo(2L);

        assertThat(resultater.get(2).antall()).isEqualTo(1L);
        assertThat(resultater.get(3).antall()).isEqualTo(1L);
    }


    @Test
    public void hentAntallOppgaverForAvdelingPerDatoTest() {
        leggInnEttSettMedOppgaver();
        var resultater = oppgaveBeholdningStatistikkTjeneste.hentAntallOppgaverForAvdelingPerDato(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(3);
        assertThat(resultater.get(0).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).behandlingType()).isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        assertThat(resultater.get(0).opprettetDato()).isEqualTo(LocalDate.now());
        assertThat(resultater.get(0).antall()).isEqualTo(4L);
        assertThat(resultater.get(1).antall()).isEqualTo(1L);
        assertThat(resultater.get(2).antall()).isEqualTo(1L);
    }

    @Test
    public void hentAntallOppgaverForAvdelingPerDatoTest2() {
        leggTilOppgave(førstegangOppgave, 27, 27);
        leggTilOppgave(førstegangOppgave2, 28, 28);//skal ikke komme i resultatssettet
        leggTilOppgave(annenAvdeling, 10, 4);//skal ikke komme i resultatssettet
        leggTilOppgave(klageOppgave, 10, 4);
        var resultater = oppgaveBeholdningStatistikkTjeneste.hentAntallOppgaverForAvdelingPerDato(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(8);
        assertThat(resultater.get(0).fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultater.get(0).opprettetDato()).isEqualTo(LocalDate.now().minusDays(27));
        resultater.remove(0);
        resultater.forEach(resultat -> assertThat(resultat.behandlingType()).isEqualTo((BehandlingType.KLAGE)));
    }

    @Test
    public void hentHentStatistikkForManueltPåVent() {
        leggInnEttSettMedOppgaveEventer();
        var resultater = oppgaveBeholdningStatistikkTjeneste.hentAntallOppgaverForAvdelingSattManueltPåVent(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(2);
        var resultatDto = resultater.get(1);
        assertThat(resultatDto.antall()).isEqualTo(2L);
        assertThat(resultatDto.behandlingFrist()).isEqualTo(LocalDate.now().plusDays(28));
        assertThat(resultatDto.fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
    }


    @Test
    public void hentOppgaverPerFørsteStønadsdag() {
        leggInnEttSettMedOppgaver();
        var resultater = oppgaveBeholdningStatistikkTjeneste.hentOppgaverPerFørsteStønadsdag(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(1);
        assertThat(resultater.get(0).førsteStønadsdag()).isEqualTo(LocalDate.now().plusMonths(1));
        assertThat(resultater.get(0).antall()).isEqualTo(6L);
    }

    private void leggInnEttSettMedOppgaveEventer() {
        var behandlingId1 = BehandlingId.random();
        var behandlingId2 = BehandlingId.random();
        var behandlingId3 = BehandlingId.random();
        var behandlingId4 = BehandlingId.random();
        var behandlingId5 = BehandlingId.random();
        var behandlingId6 = BehandlingId.random();

        entityManager.persist(Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medSystem("FPSAK")
                .medBehandlingId(behandlingId1)
                .build());
        entityManager.persist(Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medSystem("FPSAK")
                .medBehandlingId(behandlingId2)
                .build());
        entityManager.persist(Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medSystem("FPSAK")
                .medBehandlingId(behandlingId3)
                .build());
        entityManager.persist(Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medSystem("FPSAK")
                .medBehandlingId(behandlingId4)
                .build());
        entityManager.persist(Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medSystem("FPSAK")
                .medBehandlingId(behandlingId5)
                .build());
        entityManager.persist(Oppgave.builder()
                .dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medFagsakYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medSystem("FPSAK")
                .medBehandlingId(behandlingId6)
                .build());

        entityManager.persist(
                new OppgaveEventLogg(behandlingId1, OppgaveEventType.OPPRETTET, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
                new OppgaveEventLogg(behandlingId2, OppgaveEventType.OPPRETTET, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
                new OppgaveEventLogg(behandlingId3, OppgaveEventType.OPPRETTET, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
                new OppgaveEventLogg(behandlingId4, OppgaveEventType.MANU_VENT, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
                new OppgaveEventLogg(behandlingId5, OppgaveEventType.GJENAPNET, null, AVDELING_DRAMMEN_ENHET));

        //for å ungå samtidighetsproblemer med opprettettidspunkt
        entityManager.flush();
        entityManager.createNativeQuery("UPDATE OPPGAVE_EVENT_LOGG oel SET oel.OPPRETTET_TID = SYSDATE-1")
                .executeUpdate();
        entityManager.flush();

        entityManager.persist(
                new OppgaveEventLogg(behandlingId1, OppgaveEventType.LUKKET, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
                new OppgaveEventLogg(behandlingId2, OppgaveEventType.MANU_VENT, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(new OppgaveEventLogg(behandlingId3, OppgaveEventType.VENT, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
                new OppgaveEventLogg(behandlingId4, OppgaveEventType.OPPRETTET, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
                new OppgaveEventLogg(behandlingId5, OppgaveEventType.MANU_VENT, null, AVDELING_DRAMMEN_ENHET));
        entityManager.persist(
                new OppgaveEventLogg(behandlingId6, OppgaveEventType.MANU_VENT, null, AVDELING_DRAMMEN_ENHET));

        entityManager.flush();
    }

    private void leggTilOppgave(Oppgave oppgave, int startTilbakeITid, int sluttTilbakeITid) {
        entityManager.persist(oppgave);
        entityManager.flush();
        entityManager.createNativeQuery("UPDATE OPPGAVE " + "SET OPPRETTET_TID = (sysdate - " + startTilbakeITid + "), "
                + "ENDRET_TID = (sysdate - " + sluttTilbakeITid + "), " + "AKTIV = 'N' " + "WHERE ID = "
                + oppgave.getId()).executeUpdate();
    }
}
