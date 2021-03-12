package no.nav.foreldrepenger.los.admin;

import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.BERØRT_BEHANDLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandlingKlient;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class OppgaveEgenskapOppdatererTaskTest {

    private static final ForeldrepengerBehandlingKlient FPSAK_KLIENT_MOCK = mock(ForeldrepengerBehandlingKlient.class);

    private OppgaveRepository oppgaveRepository;
    private EntityManager entityManager;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        this.entityManager = entityManager;
    }

    @Test
    public void skalLeggeTilEndringssøknadEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        var prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_ENDRINGSSØKNAD.name());
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
        var prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_ENDRINGSSØKNAD.name());
        mockErEndringssøknadMedVerdi(false);
        createTask().doTask(prosessTaskData);
        var oppgaveEgenskaper = DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper).isEmpty();
    }

    @Test
    public void skalLeggeTilBerørtBehandlingEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        var prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());
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

        var prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());

        mockErBerørtBehandling();
        createTask().doTask(prosessTaskData);
        verifiserEgenskap(AndreKriterierType.BERØRT_BEHANDLING);
    }

    @Test
    public void skalIkkeSynkronisereInaktiveOppgaver() {
        var oppgave = opprettOgLagreOppgave();
        oppgave.deaktiverOppgave();
        oppgaveRepository.lagre(oppgave);

        var prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());

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
                .thenReturn(builder().medErBerørtBehandling(true).build());
    }

    private void mockErEndringssøknadMedVerdi(boolean erEndringssøknad) {
        when(FPSAK_KLIENT_MOCK.getBehandling(any(BehandlingId.class)))
                .thenReturn(builder().medErEndringssøknad(erEndringssøknad).build());
    }

    private static BehandlingFpsak.Builder builder() {
        return BehandlingFpsak.builder();
    }

}
