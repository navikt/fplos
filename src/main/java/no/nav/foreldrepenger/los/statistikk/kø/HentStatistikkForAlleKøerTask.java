package no.nav.foreldrepenger.los.statistikk.kø;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@Dependent
@ProsessTask(value = "statistikk.kø.alle", cronExpression = "0 30 5-16 * * 1-6", maxFailedRuns = 1)
public class HentStatistikkForAlleKøerTask implements ProsessTaskHandler {

    private final KøStatistikkTjeneste køStatistikkTjeneste;
    private final ProsessTaskTjeneste prosessTaskTjeneste;

    @Inject
    public HentStatistikkForAlleKøerTask(KøStatistikkTjeneste køStatistikkTjeneste, ProsessTaskTjeneste prosessTaskTjeneste) {
        this.køStatistikkTjeneste = køStatistikkTjeneste;
        this.prosessTaskTjeneste = prosessTaskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var alleKøer = køStatistikkTjeneste.hentAlleOppgavefiltreringer();
        for (var kø : alleKøer) {
            var task = ProsessTaskData.forProsessTask(HentStatistikkForKøTask.class);
            task.setProperty(HentStatistikkForKøTask.OPPGAVE_FILTER_ID, kø.getId().toString());
            prosessTaskTjeneste.lagre(task);
        }
    }
}
