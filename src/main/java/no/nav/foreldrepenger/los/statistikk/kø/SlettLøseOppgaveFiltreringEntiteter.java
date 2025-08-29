package no.nav.foreldrepenger.los.statistikk.kø;

import jakarta.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@Dependent
@ProsessTask(value = "vedlikehold.kostatistikk", cronExpression = "0 16 1 * * *", maxFailedRuns = 1)
public class SlettLøseOppgaveFiltreringEntiteter implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SlettLøseOppgaveFiltreringEntiteter.class);
    private final EntityManager entityManager;

    @Inject
    public SlettLøseOppgaveFiltreringEntiteter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var utdaterteAndreKriterier = slettLøseKriterier();
        LOG.info("Slettet {} løse kriterier i filtrering-kriterier-tabellen", utdaterteAndreKriterier);
        var utdaterteYtelseTyper = slettLøseYtelseTyper();
        LOG.info("Slettet {} løse ytelsetyper i filtrering-ytelsetyper-tabellen", utdaterteYtelseTyper);
        var utdaterteBehandlingTyper = slettLøseBehandlingsTyper();
        LOG.info("Slettet {} løse behandlingstyper i filtrering-behandling-type-tabellen", utdaterteBehandlingTyper);
    }

    int slettLøseKriterier() {
        var query = entityManager.createNativeQuery("delete from FILTRERING_ANDRE_KRITERIER where oppgave_filtrering_id not in (select id from OPPGAVE_FILTRERING)");
        int deletedRows = query.executeUpdate();
        entityManager.flush();
        return deletedRows;
    }

    int slettLøseYtelseTyper() {
        var query = entityManager.createNativeQuery("delete from FILTRERING_YTELSE_TYPE where oppgave_filtrering_id not in (select id from OPPGAVE_FILTRERING)");
        int deletedRows = query.executeUpdate();
        entityManager.flush();
        return deletedRows;
    }

    int slettLøseBehandlingsTyper() {
        var query = entityManager.createNativeQuery("delete from FILTRERING_BEHANDLING_TYPE where oppgave_filtrering_id not in (select id from OPPGAVE_FILTRERING)");
        int deletedRows = query.executeUpdate();
        entityManager.flush();
        return deletedRows;
    }
}
