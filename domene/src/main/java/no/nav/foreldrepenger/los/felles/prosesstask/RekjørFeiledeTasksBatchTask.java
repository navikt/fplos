package no.nav.foreldrepenger.los.felles.prosesstask;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "retry.feilendeTasks", cronExpression = "0 20 7 * * *", maxFailedRuns = 1)
public class RekjørFeiledeTasksBatchTask implements ProsessTaskHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RekjørFeiledeTasksBatchTask.class);
    private final BatchProsessTaskRepository taskRepository;

    @Inject
    public RekjørFeiledeTasksBatchTask(BatchProsessTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var rekjørAlleFeiledeTasks = taskRepository.rekjørAlleFeiledeTasks();
        LOG.info("Rekjører alle feilende tasks, oppdaterte {} tasks", rekjørAlleFeiledeTasks);
    }
}
