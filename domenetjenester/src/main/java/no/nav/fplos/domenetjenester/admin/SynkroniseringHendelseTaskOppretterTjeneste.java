package no.nav.fplos.domenetjenester.admin;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.log.mdc.MDCOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@ApplicationScoped
public class SynkroniseringHendelseTaskOppretterTjeneste {

    private static final Logger log = LoggerFactory.getLogger(SynkroniseringHendelseTaskOppretterTjeneste.class);

    private ProsessTaskRepository prosessTaskRepository;

    SynkroniseringHendelseTaskOppretterTjeneste() {
        // for CDI proxy
    }

    @Inject
    public SynkroniseringHendelseTaskOppretterTjeneste(ProsessTaskRepository prosessTaskRepository) {
        this.prosessTaskRepository = prosessTaskRepository;
    }

    public String opprettOppgaveEgenskapOppdatererTask(List<BehandlingId> behandlinger) {
        if (behandlinger.size() > 1000) {
            return "For stor beholdning, send under 1000";
        }

        final String callId = (MDCOperations.getCallId() == null ? MDCOperations.generateCallId() : MDCOperations.getCallId()) + "_";

        log.info("Oppretter tasker for synkronisering av {} hendelser", behandlinger.size());
        var kjøres = LocalDateTime.now();
        for (var behandling : behandlinger) {
            opprettSynkroniseringTask(behandling, callId, kjøres);
            kjøres = kjøres.plus(500, ChronoUnit.MILLIS);
        }
        return OppgaveEgenskapOppdatererTask.TASKTYPE + "-" + behandlinger.size();
    }

    private void opprettSynkroniseringTask(BehandlingId behandlingId, String callId, LocalDateTime kjøretidspunkt) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(SynkroniseringHendelseTask.TASKTYPE);
        prosessTaskData.setCallId(callId + behandlingId.toString());
        prosessTaskData.setPrioritet(999);
        prosessTaskData.setNesteKjøringEtter(kjøretidspunkt);
        prosessTaskData.setProperty(ProsessTaskData.BEHANDLING_ID, behandlingId.toString());
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
