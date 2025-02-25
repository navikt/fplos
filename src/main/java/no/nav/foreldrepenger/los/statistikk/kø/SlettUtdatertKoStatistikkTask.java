package no.nav.foreldrepenger.los.statistikk.kø;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@Dependent
@ProsessTask(value = "vedlikehold.kostatistikk", cronExpression = "0 16 1 * * *", maxFailedRuns = 1)
public class SlettUtdatertKoStatistikkTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SlettUtdatertKoStatistikkTask.class);
    private final KøStatistikkRepository køStatistikkRepository;

    @Inject
    public SlettUtdatertKoStatistikkTask(KøStatistikkRepository køStatistikkRepository) {
        this.køStatistikkRepository = køStatistikkRepository;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var slettet = køStatistikkRepository.slettUtdaterte();
        LOG.info("Slettet {} rader i køstatistikk-tabellen", slettet);
        var utdatert = køStatistikkRepository.slettLøsStatistikk();
        LOG.info("Slettet {} løse innslag i køstatistikk-tabellen", utdatert);
        var utdaterteAndreKriterier = køStatistikkRepository.slettLøseKriterier();
        LOG.info("Slettet {} løse kriterier i filtrering-kriterier-tabellen", utdaterteAndreKriterier);
        var utdaterteYtelseTyper = køStatistikkRepository.slettLøseYtelseTyper();
        LOG.info("Slettet {} løse ytelsetyper i filtrering-ytelsetyper-tabellen", utdaterteYtelseTyper);
        var utdaterteBehandlingTyper = køStatistikkRepository.slettLøseBehandlingsTyper();
        LOG.info("Slettet {} løse behandlingstyper i filtrering-behandling-type-tabellen", utdaterteBehandlingTyper);
    }
}
