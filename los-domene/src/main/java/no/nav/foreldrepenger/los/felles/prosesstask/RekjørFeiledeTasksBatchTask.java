package no.nav.foreldrepenger.los.felles.prosesstask;

import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@ProsessTask(RekjørFeiledeTasksBatchTask.TASKTYPE)
public class RekjørFeiledeTasksBatchTask implements ProsessTaskHandler {
    public static final String TASKTYPE = "retry.feilendeTasks";
    private static final Logger log = LoggerFactory.getLogger(RekjørFeiledeTasksBatchTask.class);
    private BatchProsessTaskRepository taskRepository;

    @Inject
    public RekjørFeiledeTasksBatchTask(BatchProsessTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        int rekjørAlleFeiledeTasks = taskRepository.rekjørAlleFeiledeTasks();
        log.info("Rekjører alle feilende tasks, oppdaterte {} tasks", rekjørAlleFeiledeTasks);
    }
}
