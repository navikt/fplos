package no.nav.fplos.admin;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.BERØRT_BEHANDLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareTest;
import no.nav.vedtak.felles.testutilities.db.Repository;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class OppgaveEgenskapOppdatererTaskTest extends EntityManagerAwareTest {

    private static final ForeldrepengerBehandlingRestKlient FPSAK_KLIENT_MOCK = mock(ForeldrepengerBehandlingRestKlient.class);

    private OppgaveRepository oppgaveRepository;
    private Repository repository;


    @BeforeEach
    public void setup() {
        var entityManager = getEntityManager();
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        repository = new Repository(entityManager);
    }

    @Test
    public void skalLeggeTilEndringssøknadEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
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
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_ENDRINGSSØKNAD.name());
        mockErEndringssøknadMedVerdi(false);
        createTask().doTask(prosessTaskData);
        var oppgaveEgenskaper = repository.hentAlle(OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper.size()).isEqualTo(0);
    }

    @Test
    public void skalLeggeTilBerørtBehandlingEgenskap() {
        var oppgave = opprettOgLagreOppgave();
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, OppgaveEgenskapTypeMapper.EGENSKAP_BERØRTBEHANDLING.name());
        mockErBerørtBehandling();
        createTask().doTask(prosessTaskData);
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
        createTask().doTask(prosessTaskData);
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
        createTask().doTask(prosessTaskData);
        var oppgaveEgenskaper = repository.hentAlle(OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper.size()).isEqualTo(0);
    }

    private void verifiserEgenskap(AndreKriterierType type) {
        List<OppgaveEgenskap> oppgaveEgenskaper = repository.hentAlle(OppgaveEgenskap.class);
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
