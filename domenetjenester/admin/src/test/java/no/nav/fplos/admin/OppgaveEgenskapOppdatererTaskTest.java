package no.nav.fplos.admin;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.List;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.BERØRT_BEHANDLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(CdiRunner.class)
public class OppgaveEgenskapOppdatererTaskTest {
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    @Inject
    private OppgaveRepository oppgaveRepository;

    private OppgaveEgenskapOppdatererTask task;
    private final ForeldrepengerBehandlingRestKlient fpsakMock = mock(ForeldrepengerBehandlingRestKlient.class);

    @Before
    public void createTask() {
        task = new OppgaveEgenskapOppdatererTask(oppgaveRepository, fpsakMock);
    }

    @Test
    public void skalLeggeTilEndringssøknadEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_ENDRINGSSØKNAD.name());
        mockErEndringssøknadMedVerdi(true);
        task.doTask(prosessTaskData);
        verifiserEgenskap(AndreKriterierType.ENDRINGSSØKNAD);
    }

    @Test
    public void skalIkkeLeggeTilEgenskapNårIkkeAktuelt() {
        var oppgave = opprettOgLagreOppgave();
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_ENDRINGSSØKNAD.name());
        mockErEndringssøknadMedVerdi(false);
        task.doTask(prosessTaskData);
        var oppgaveEgenskaper = repoRule.getRepository().hentAlle(OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper.size()).isEqualTo(0);
    }

    @Test
    public void skalLeggeTilBerørtBehandlingEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());
        mockErBerørtBehandling();
        task.doTask(prosessTaskData);
        verifiserEgenskap(AndreKriterierType.BERØRT_BEHANDLING);
    }

    @Test
    public void skalReaktivereInaktivEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        OppgaveEgenskap egenskap = new OppgaveEgenskap(oppgave, BERØRT_BEHANDLING);
        egenskap.deaktiverOppgaveEgenskap();
        oppgaveRepository.lagre(egenskap);

        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());

        mockErBerørtBehandling();
        task.doTask(prosessTaskData);
        verifiserEgenskap(AndreKriterierType.BERØRT_BEHANDLING);
    }

    @Test
    public void skalIkkeSynkronisereInaktiveOppgaver() {
        var oppgave = opprettOgLagreOppgave();
        oppgave.deaktiverOppgave();
        oppgaveRepository.lagre(oppgave);

        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());

        mockErBerørtBehandling();
        task.doTask(prosessTaskData);
        var oppgaveEgenskaper = repoRule.getRepository().hentAlle(OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper.size()).isEqualTo(0);
    }

    private void verifiserEgenskap(AndreKriterierType type) {
        List<OppgaveEgenskap> oppgaveEgenskaper = repoRule.getRepository().hentAlle(OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper.size()).isEqualTo(1);
        assertThat(oppgaveEgenskaper.get(0).getAktiv()).isEqualTo(true);
        assertThat(oppgaveEgenskaper.get(0).getAndreKriterierType()).isEqualTo(type);
    }

    private Oppgave opprettOgLagreOppgave() {
        var oppgave = Oppgave.builder().dummyOppgave("4404").medSystem("FPSAK").build();
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    private void mockErBerørtBehandling() {
        when(fpsakMock.getBehandling(any(BehandlingId.class)))
                .thenReturn(builder().medErBerørtBehandling(true).build());
    }

    private void mockErEndringssøknadMedVerdi(boolean erEndringssøknad) {
        when(fpsakMock.getBehandling(any(BehandlingId.class)))
                .thenReturn(builder().medErEndringssøknad(erEndringssøknad).build());
    }

    private static BehandlingFpsak.Builder builder() {
        return BehandlingFpsak.builder();
    }

}
