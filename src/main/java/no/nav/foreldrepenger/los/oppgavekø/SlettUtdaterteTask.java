package no.nav.foreldrepenger.los.oppgavekø;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.oppgave.BehandlingTilstand;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@Dependent
@ProsessTask(value = "vedlikehold.slettutdaterte", cronExpression = "30 15 2 15 * *", maxFailedRuns = 1)
public class SlettUtdaterteTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SlettUtdaterteTask.class);

    private static final String FØR = "foer";

    private final EntityManager entityManager;

    @Inject
    public SlettUtdaterteTask(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var før = LocalDate.now().minusMonths(3).withDayOfMonth(1).atStartOfDay();
        int slettetReservasjonerAntall = slettEldreUtløpteReservasjoner(før);
        LOG.info("LOS vedlikehold: Slettet {} utdaterte reservasjoner", slettetReservasjonerAntall);

        var slettetOppgaverAntall = slettEldreUtløpteOppgaver(før);
        LOG.info("LOS vedlikehold: Slettet {} utdaterte oppgaver", slettetOppgaverAntall);

        var slettetBehandlingerAntall = slettEldreUtløpteBehandlinger(før);
        LOG.info("LOS vedlikehold: Slettet {} utdaterte behandlinger", slettetBehandlingerAntall);

        slettEldreUtløptStatistikk(før);
        entityManager.flush();
    }

    private int slettEldreUtløpteReservasjoner(LocalDateTime før) {
        return entityManager.createQuery("delete from Reservasjon where reservertTil < :foer")
            .setParameter(FØR, før)
            .executeUpdate();
    }

    private int slettEldreUtløpteOppgaver(LocalDateTime før) {
        entityManager.createQuery("delete from Reservasjon where oppgave.id in" +
                " (select id from Oppgave where aktiv = false AND coalesce(endretTidspunkt, opprettetTidspunkt) < :foer)")
            .setParameter(FØR, før)
            .executeUpdate();
        entityManager.createQuery("delete from OppgaveEgenskap where oppgave.id in" +
                " (select id from Oppgave where aktiv = false AND coalesce(endretTidspunkt, opprettetTidspunkt) < :foer)")
            .setParameter(FØR, før)
            .executeUpdate();
        return entityManager.createQuery("delete from Oppgave where aktiv = false AND coalesce(endretTidspunkt, opprettetTidspunkt) < :foer")
            .setParameter(FØR, før)
            .executeUpdate();
    }

    private int slettEldreUtløpteBehandlinger(LocalDateTime før) {
        entityManager.createQuery("delete from BehandlingEgenskap where behandlingId in" +
                " (select id from Behandling where behandlingTilstand = :avsluttet AND avsluttet < :foer)")
            .setParameter(FØR, før)
            .setParameter("avsluttet", BehandlingTilstand.AVSLUTTET)
            .executeUpdate();
        return entityManager.createQuery("delete from Behandling where behandlingTilstand = :avsluttet AND avsluttet < :foer")
            .setParameter(FØR, før)
            .setParameter("avsluttet", BehandlingTilstand.AVSLUTTET)
            .executeUpdate();
    }

    private void slettEldreUtløptStatistikk(LocalDateTime før) {
        var instantms = før.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        entityManager.createQuery("delete from StatistikkEnhetYtelseBehandling where tidsstempel < :instantms")
            .setParameter("instantms", instantms)
            .executeUpdate();
        entityManager.createQuery("delete from StatistikkOppgaveFilter where tidsstempel < :instantms")
            .setParameter("instantms", instantms)
            .executeUpdate();
    }
}
