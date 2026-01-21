package no.nav.foreldrepenger.los.statistikk.kø;


import java.time.LocalDate;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.statistikk.StatistikkRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@Dependent
@ProsessTask(value = "statistikk.kø.enkelt", maxFailedRuns = 1)
public class HentStatistikkForKøTask implements ProsessTaskHandler {
    static final String OPPGAVE_FILTER_ID = "oppgave_filter_id";

    private KøStatistikkTjeneste køStatistikkTjeneste;
    private StatistikkRepository statistikkRepository;

    public HentStatistikkForKøTask() {
        // for CDI
    }

    @Inject
    public HentStatistikkForKøTask(KøStatistikkTjeneste køStatistikkTjeneste, StatistikkRepository statistikkRepository) {
        this.køStatistikkTjeneste = køStatistikkTjeneste;
        this.statistikkRepository = statistikkRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingsKø = Long.valueOf(prosessTaskData.getPropertyValue(OPPGAVE_FILTER_ID));
        var antallOppgaver = køStatistikkTjeneste.hentAntallOppgaver(behandlingsKø);
        var antallTilgjengeligeOppgaver = køStatistikkTjeneste.hentAntallTilgjengeligeOppgaverFor(behandlingsKø);
        var statistikkOppgaveFilter = new StatistikkOppgaveFilter(behandlingsKø, System.currentTimeMillis(), LocalDate.now(), antallOppgaver, antallTilgjengeligeOppgaver, InnslagType.REGELMESSIG);
        statistikkRepository.lagreStatistikkOppgaveFilter(statistikkOppgaveFilter);
    }
}
