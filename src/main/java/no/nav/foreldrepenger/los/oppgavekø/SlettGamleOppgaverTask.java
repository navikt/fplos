package no.nav.foreldrepenger.los.oppgavekø;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@Dependent
@ProsessTask(value = "vedlikehold.slettgamleoppgaver", maxFailedRuns = 1)
public class SlettGamleOppgaverTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SlettGamleOppgaverTask.class);

    private static final String MND_TILBAKE = "mndTilbake";

    private final EntityManager entityManager;
    private final ProsessTaskTjeneste prosessTaskTjeneste;

    @Inject
    public SlettGamleOppgaverTask(EntityManager entityManager, ProsessTaskTjeneste prosessTaskTjeneste) {
        this.entityManager = entityManager;
        this.prosessTaskTjeneste = prosessTaskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var mndTilbake = Optional.ofNullable(prosessTaskData.getPropertyValue(MND_TILBAKE)).map(Long::valueOf).orElse(100L);
        var antallSlettet = slettEldreUtløpteOppgaver(mndTilbake);
        LOG.info("Slettet {} utdaterte oppgaver eldre enn {} måneder", antallSlettet, mndTilbake);
        if (mndTilbake > 13) {
            var nesteTask = ProsessTaskData.forProsessTask(SlettGamleOppgaverTask.class);
            nesteTask.setProperty(MND_TILBAKE, String.valueOf(mndTilbake - 1));
            prosessTaskTjeneste.lagre(nesteTask);
        }
    }

    private int slettEldreUtløpteOppgaver(Long mndTilbake) {
        var før = LocalDate.now().minusMonths(mndTilbake).atStartOfDay();
        entityManager.createNativeQuery("delete from RESERVASJON where oppgave_id in (select id from OPPGAVE where aktiv = 'N' AND coalesce(endret_tid, opprettet_tid) < :foer)")
            .setParameter("foer", før)
            .executeUpdate();
        entityManager.createNativeQuery("delete from OPPGAVE_EGENSKAP where oppgave_id in (select id from OPPGAVE where aktiv = 'N' AND coalesce(endret_tid, opprettet_tid) < :foer)")
            .setParameter("foer", før)
            .executeUpdate();
        var antall = entityManager.createNativeQuery("delete from OPPGAVE where aktiv = 'N' AND coalesce(endret_tid, opprettet_tid) < :foer")
            .setParameter("foer", før)
            .executeUpdate();
        entityManager.flush();
        return antall;
    }
}
