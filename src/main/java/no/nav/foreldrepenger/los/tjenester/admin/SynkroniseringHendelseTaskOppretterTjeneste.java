package no.nav.foreldrepenger.los.tjenester.admin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.BehandlingHendelseTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
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

    public int opprettOppgaveEgenskapOppdatererTasks(List<KildeBehandlingId> behandlinger) {
        if (behandlinger.size() > 1000) {
            throw new IllegalArgumentException("Støtter ikke så mange behandlinger, send under 1000");
        }

        final var callId = (MDCOperations.getCallId() == null ? MDCOperations.generateCallId() : MDCOperations.getCallId()) + "_";

        LOG.info("Oppretter tasker for synkronisering av {} hendelser", behandlinger.size());
        var kjøres = LocalDateTime.now();
        for (var behandling : behandlinger) {
            opprettSynkroniseringTask(behandling, callId, kjøres);
            kjøres = kjøres.plus(500, ChronoUnit.MILLIS);
        }
        return behandlinger.size();
    }

    public record KildeBehandlingId(Kildesystem kildesystem, BehandlingId behandlingId) {
    }

    private void opprettSynkroniseringTask(KildeBehandlingId kildeBehandlingId, String callId, LocalDateTime kjøretidspunkt) {
        var prosessTaskData = ProsessTaskData.forProsessTask(BehandlingHendelseTask.class);
        prosessTaskData.setCallId(callId + kildeBehandlingId.behandlingId.toString());
        prosessTaskData.setPrioritet(4);
        prosessTaskData.setNesteKjøringEtter(kjøretidspunkt);
        prosessTaskData.setProperty(BehandlingHendelseTask.HENDELSE_UUID, UUID.randomUUID().toString());
        prosessTaskData.setProperty(BehandlingHendelseTask.BEHANDLING_UUID, kildeBehandlingId.behandlingId.toString());
        prosessTaskData.setProperty(BehandlingHendelseTask.KILDE, kildeBehandlingId.kildesystem.name());
        prosessTaskTjeneste.lagre(prosessTaskData);
    }
}
