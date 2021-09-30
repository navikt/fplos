package no.nav.foreldrepenger.los.admin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.log.mdc.MDCOperations;

@ApplicationScoped
public class SynkroniseringHendelseTaskOppretterTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(SynkroniseringHendelseTaskOppretterTjeneste.class);

    private ProsessTaskTjeneste prosessTaskTjeneste;

    @Inject
    public SynkroniseringHendelseTaskOppretterTjeneste(ProsessTaskTjeneste prosessTaskTjeneste) {
        this.prosessTaskTjeneste = prosessTaskTjeneste;
    }

    SynkroniseringHendelseTaskOppretterTjeneste() {
        // for CDI proxy
    }

    public int opprettOppgaveEgenskapOppdatererTask(List<BehandlingId> behandlinger) {
        if (behandlinger.size() > 1000) {
            throw new IllegalArgumentException("Støtter ikke så mange behandlinger, send under 1000");
        }

        final var callId =
                (MDCOperations.getCallId() == null ? MDCOperations.generateCallId() : MDCOperations.getCallId()) + "_";

        LOG.info("Oppretter tasker for synkronisering av {} hendelser", behandlinger.size());
        var kjøres = LocalDateTime.now();
        for (var behandling : behandlinger) {
            opprettSynkroniseringTask(behandling, callId, kjøres);
            kjøres = kjøres.plus(500, ChronoUnit.MILLIS);
        }
        return behandlinger.size();
    }

    private void opprettSynkroniseringTask(BehandlingId behandlingId, String callId, LocalDateTime kjøretidspunkt) {
        var prosessTaskData = ProsessTaskData.forProsessTask(SynkroniseringHendelseTask.class);
        prosessTaskData.setCallId(callId + behandlingId.toString());
        prosessTaskData.setPrioritet(999);
        prosessTaskData.setNesteKjøringEtter(kjøretidspunkt);
        prosessTaskData.setProperty(SynkroniseringHendelseTask.BEHANDLING_ID_TASK_KEY, behandlingId.toString());
        prosessTaskTjeneste.lagre(prosessTaskData);
    }
}
