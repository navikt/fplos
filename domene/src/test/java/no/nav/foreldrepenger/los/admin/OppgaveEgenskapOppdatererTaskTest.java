package no.nav.foreldrepenger.los.admin;

import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.BERØRT_BEHANDLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.dbstøtte.DBTestUtil;
import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandling;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ExtendWith(JpaExtension.class)
public class OppgaveEgenskapOppdatererTaskTest {

    private static final ForeldrepengerBehandling FPSAK_KLIENT_MOCK = mock(ForeldrepengerBehandling.class);

    private OppgaveRepository oppgaveRepository;
    private EntityManager entityManager;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepository(entityManager);
        this.entityManager = entityManager;
    }

    @Test
    public void skalLeggeTilEndringssøknadEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        var prosessTaskData = ProsessTaskData.forProsessTask(OppgaveEgenskapOppdatererTask.class);
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.OPPGAVE_ID_TASK_KEY, String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER_TASK_KEY, OppgaveEgenskapTypeMapper.EGENSKAP_ENDRINGSSØKNAD.name());
        mockErEndringssøknadMedVerdi(true);
        createTask().doTask(prosessTaskData);
        verifiserEgenskap(AndreKriterierType.ENDRINGSSØKNAD);
    }

    private ProsessTaskHandler createTask() {
        return new OppgaveEgenskapOppdatererTask(oppgaveRepository, FPSAK_KLIENT_MOCK);
    }

    @Test
    public void skalIkkeLeggeTilEgenskapNårIkkeAktuelt() {
        var oppgave = opprettOgLagreOppgave();
        var prosessTaskData = ProsessTaskData.forProsessTask(OppgaveEgenskapOppdatererTask.class);
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.OPPGAVE_ID_TASK_KEY, String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER_TASK_KEY, OppgaveEgenskapTypeMapper.EGENSKAP_ENDRINGSSØKNAD.name());
        mockErEndringssøknadMedVerdi(false);
        createTask().doTask(prosessTaskData);
        var oppgaveEgenskaper = DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper).isEmpty();
    }

    @Test
    public void skalLeggeTilBerørtBehandlingEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        var prosessTaskData = ProsessTaskData.forProsessTask(OppgaveEgenskapOppdatererTask.class);
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.OPPGAVE_ID_TASK_KEY, String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER_TASK_KEY, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());
        mockErBerørtBehandling();
        createTask().doTask(prosessTaskData);
        verifiserEgenskap(AndreKriterierType.BERØRT_BEHANDLING);
    }

    @Test
    public void skalReaktivereInaktivEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        var egenskap = new OppgaveEgenskap(oppgave, BERØRT_BEHANDLING);
        egenskap.deaktiverOppgaveEgenskap();
        oppgaveRepository.lagre(egenskap);

        var prosessTaskData = ProsessTaskData.forProsessTask(OppgaveEgenskapOppdatererTask.class);
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.OPPGAVE_ID_TASK_KEY, String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER_TASK_KEY, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());

        mockErBerørtBehandling();
        createTask().doTask(prosessTaskData);
        verifiserEgenskap(AndreKriterierType.BERØRT_BEHANDLING);
    }

    @Test
    public void skalIkkeSynkronisereInaktiveOppgaver() {
        var oppgave = opprettOgLagreOppgave();
        oppgave.avsluttOppgave();
        oppgaveRepository.lagre(oppgave);

        var prosessTaskData = ProsessTaskData.forProsessTask(OppgaveEgenskapOppdatererTask.class);
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.OPPGAVE_ID_TASK_KEY, String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER_TASK_KEY, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());

        mockErBerørtBehandling();
        createTask().doTask(prosessTaskData);
        var oppgaveEgenskaper = DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper).isEmpty();
    }

    private void verifiserEgenskap(AndreKriterierType type) {
        var oppgaveEgenskaper = DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper).hasSize(1);
        assertThat(oppgaveEgenskaper.get(0).getAktiv()).isEqualTo(true);
        assertThat(oppgaveEgenskaper.get(0).getAndreKriterierType()).isEqualTo(type);
    }

    private Oppgave opprettOgLagreOppgave() {
        var oppgave = Oppgave.builder().dummyOppgave("4404").medSystem("FPSAK").build();
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    private void mockErBerørtBehandling() {
        when(FPSAK_KLIENT_MOCK.getBehandling(any(BehandlingId.class)))
                .thenReturn(builder().medAktørId(AktørId.dummy()).medErBerørtBehandling(true).build());
    }

    private void mockErEndringssøknadMedVerdi(boolean erEndringssøknad) {
        when(FPSAK_KLIENT_MOCK.getBehandling(any(BehandlingId.class)))
                .thenReturn(builder().medAktørId(AktørId.dummy()).medErEndringssøknad(erEndringssøknad).build());
    }

    private static BehandlingFpsak.Builder builder() {
        return BehandlingFpsak.builder();
    }

}
