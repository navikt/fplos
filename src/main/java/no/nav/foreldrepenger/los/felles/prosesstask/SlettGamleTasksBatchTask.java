package no.nav.foreldrepenger.los.felles.prosesstask;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.MottattHendelseRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@Dependent
@ProsessTask(value = "vedlikehold.tasks.slettgamle", cronExpression = "0 30 1 * * *", maxFailedRuns = 1)
public class SlettGamleTasksBatchTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SlettGamleTasksBatchTask.class);

    private final ProsessTaskTjeneste prosessTaskTjeneste;
    private final MottattHendelseRepository hendelseRepository;

    @Inject
    public SlettGamleTasksBatchTask(ProsessTaskTjeneste prosessTaskTjeneste, MottattHendelseRepository hendelseRepository) {
        this.prosessTaskTjeneste = prosessTaskTjeneste;
        this.hendelseRepository = hendelseRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var slettet = prosessTaskTjeneste.slettÅrsgamleFerdige();
        LOG.info("Slettet {} tasks som er over ett år gamle.", slettet);
        hendelseRepository.slettMånedsGamle();
    }
}
