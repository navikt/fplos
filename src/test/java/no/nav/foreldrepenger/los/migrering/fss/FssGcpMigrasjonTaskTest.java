package no.nav.foreldrepenger.los.migrering.fss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.los.migrering.dto.BehandlingDataDto;
import no.nav.foreldrepenger.los.migrering.dto.BulkDataWrapper;
import no.nav.foreldrepenger.los.migrering.dto.StatEnhetYtelseBehandlingDataDto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@ExtendWith(MockitoExtension.class)
class FssGcpMigrasjonTaskTest {

    @Mock
    private FssEksportRepository fssEksportRepository;
    @Mock
    private GcpLosKlient gcpLosKlient;
    @Mock
    private ProsessTaskTjeneste prosessTaskTjeneste;

    private FssGcpMigrasjonTask task;

    @BeforeEach
    void setup() {
        task = new FssGcpMigrasjonTask(fssEksportRepository, gcpLosKlient, prosessTaskTjeneste);
    }


    @Test
    void skal_paginere_riktig_og_lage_neste_steg_task_nar_ferdig() {
        when(fssEksportRepository.hentBehandlinger(anyInt(), eq(100)))
            .thenReturn(behandlinger(100))
            .thenReturn(behandlinger(100))
            .thenReturn(behandlinger(40));

        var data = ProsessTaskData.forProsessTask(FssGcpMigrasjonTask.class);
        data.setProperty(FssGcpMigrasjonTask.STEG, FssGcpMigrasjonTask.MigreringSteg.DEL2_BEHANDLINGER.name());

        task.doTask(data);

        InOrder inOrder = inOrder(fssEksportRepository);
        inOrder.verify(fssEksportRepository).hentBehandlinger(0, 100);
        inOrder.verify(fssEksportRepository).hentBehandlinger(100, 100);
        inOrder.verify(fssEksportRepository).hentBehandlinger(200, 100);

        verify(gcpLosKlient, times(3)).lagreBulkData(any());

        var captor = ArgumentCaptor.forClass(ProsessTaskData.class);
        verify(prosessTaskTjeneste).lagre(captor.capture());

        assertThat(captor.getValue().getPropertyValue(FssGcpMigrasjonTask.STEG))
            .isEqualTo(FssGcpMigrasjonTask.MigreringSteg.DEL3_AKTIVE_OPPGAVER.name());
        assertThat(captor.getValue().getPropertyValue(FssGcpMigrasjonTask.START_POSISJON)).isNull();
    }

    @Test
    void skal_fortsette_i_ny_task_nar_terskel_passeres() {
        when(fssEksportRepository.hentBehandlinger(anyInt(), eq(100)))
            .thenReturn(behandlinger(100));

        var data = ProsessTaskData.forProsessTask(FssGcpMigrasjonTask.class);
        data.setProperty(FssGcpMigrasjonTask.STEG, FssGcpMigrasjonTask.MigreringSteg.DEL2_BEHANDLINGER.name());

        task.doTask(data);

        verify(fssEksportRepository, times(20)).hentBehandlinger(anyInt(), eq(100));
        verify(gcpLosKlient, times(20)).lagreBulkData(any());

        var captor = ArgumentCaptor.forClass(ProsessTaskData.class);
        verify(prosessTaskTjeneste, times(1)).lagre(captor.capture());

        assertThat(captor.getValue().getPropertyValue(FssGcpMigrasjonTask.STEG))
            .isEqualTo(FssGcpMigrasjonTask.MigreringSteg.DEL2_BEHANDLINGER.name());
        assertThat(captor.getValue().getPropertyValue(FssGcpMigrasjonTask.START_POSISJON)).isEqualTo("2000");
    }

    @Test
    void skal_bruke_startposisjon_fra_prosesstaskdata() {
        when(fssEksportRepository.hentBehandlinger(anyInt(), eq(100)))
            .thenReturn(behandlinger(100))
            .thenReturn(behandlinger(40)); // antall < bulk_størrelse trigger neste steg

        var data = ProsessTaskData.forProsessTask(FssGcpMigrasjonTask.class);
        data.setProperty(FssGcpMigrasjonTask.STEG, FssGcpMigrasjonTask.MigreringSteg.DEL2_BEHANDLINGER.name());
        data.setProperty(FssGcpMigrasjonTask.START_POSISJON, "6000");

        task.doTask(data);

        InOrder inOrder = inOrder(fssEksportRepository);
        inOrder.verify(fssEksportRepository).hentBehandlinger(6000, 100);
        inOrder.verify(fssEksportRepository).hentBehandlinger(6100, 100);

        verify(gcpLosKlient, times(2)).lagreBulkData(any());

        var captor = ArgumentCaptor.forClass(ProsessTaskData.class);
        verify(prosessTaskTjeneste, times(1)).lagre(captor.capture());

        assertThat(captor.getValue().getPropertyValue(FssGcpMigrasjonTask.STEG))
            .isEqualTo(FssGcpMigrasjonTask.MigreringSteg.DEL3_AKTIVE_OPPGAVER.name());
        assertThat(captor.getValue().getPropertyValue(FssGcpMigrasjonTask.START_POSISJON)).isNull();
    }

    @Test
    void skal_kjore_del1_og_lage_task_for_del2() {
        // regresjonstest av overgang del1/del2 etter innføring av flere taskinstanser per del
        when(fssEksportRepository.hentOrganisasjonOgKøer())
            .thenReturn(BulkDataWrapper.organisasjonOgKøOppset(null, List.of()));

        var data = ProsessTaskData.forProsessTask(FssGcpMigrasjonTask.class);
        data.setProperty(FssGcpMigrasjonTask.STEG, FssGcpMigrasjonTask.MigreringSteg.DEL1_ORGANISASJON_OG_KØ.name());

        task.doTask(data);

        verify(fssEksportRepository).hentOrganisasjonOgKøer();
        verify(gcpLosKlient, times(1)).lagreBulkData(any());

        var captor = ArgumentCaptor.forClass(ProsessTaskData.class);
        verify(prosessTaskTjeneste, times(1)).lagre(captor.capture());

        assertThat(captor.getValue().getPropertyValue(FssGcpMigrasjonTask.STEG))
            .isEqualTo(FssGcpMigrasjonTask.MigreringSteg.DEL2_BEHANDLINGER.name());
        assertThat(captor.getValue().getPropertyValue(FssGcpMigrasjonTask.START_POSISJON)).isNull();
    }

    @Test
    void skal_ikke_lage_ny_task_nar_del6_gar_til_del7() {
        // regresjonstest av overgang fra del6 etter innføring av flere taskinstanser per del
        when(fssEksportRepository.hentStatistikkEnhetYtelseBehandling(anyInt(), eq(100)))
            .thenReturn(statistikkEnhetYtelseBehandling(40));

        var data = ProsessTaskData.forProsessTask(FssGcpMigrasjonTask.class);
        data.setProperty(FssGcpMigrasjonTask.STEG, FssGcpMigrasjonTask.MigreringSteg.DEL6_STATISTIKK_EYB.name());

        task.doTask(data);

        verify(fssEksportRepository).hentStatistikkEnhetYtelseBehandling(0, 100);
        verify(gcpLosKlient, times(1)).lagreBulkData(any());
        verifyNoInteractions(prosessTaskTjeneste);
    }

    private static BulkDataWrapper behandlinger(int antall) {
        List<BehandlingDataDto> list = Collections.nCopies(antall, null);
        return BulkDataWrapper.behandlinger(list);
    }

    private static BulkDataWrapper statistikkEnhetYtelseBehandling(int antall) {
        List<StatEnhetYtelseBehandlingDataDto> list = Collections.nCopies(antall, null);
        return BulkDataWrapper.statistikkEnhetYtelseBehandling(list);
    }
}
