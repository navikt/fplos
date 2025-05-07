package no.nav.foreldrepenger.los.oppgave.tilbudtoppgave;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class TilbudtOppgaveRepository {

    private EntityManager entityManager;

    public TilbudtOppgaveRepository() {
    }

    @Inject
    public TilbudtOppgaveRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // henter behandlinger oftest tilbudt saksbehandlere (proxy for sannsynlig upopulær behandling)
    // gitt mye frem og tilbake på vent eller mange AP kan man eksperimentere med kompenserende faktor som hensyntar antall oppgaver, feks faktor på 1/n-oppgaver e.l
    public Map<BehandlingId, Integer> toppUplukkedeBehandlinger() {
        var upoppeBehandlinger = entityManager.createNativeQuery("""
            with aktuelleBehandlinger as (
                select abo.behandling_id
                from oppgave abo
                where abo.aktiv = 'J'
                and not exists (
                    select 1
                        from reservasjon r
                        where r.oppgave_id = abo.id
                        and r.reservert_til > current_timestamp
                )
            )
            select
              o.behandling_id,
              sum(coalesce(o.tilbudt_count, 0)) AS total_count
            from oppgave o
            join aktuelleBehandlinger on aktuelleBehandlinger.behandling_id = o.behandling_id
            group by o.behandling_id
            order by total_count desc
            """);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = upoppeBehandlinger.setMaxResults(10).getResultList();

        Map<BehandlingId,Integer> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            var rawBehandlingId = (byte[]) row[0];
            var bb = ByteBuffer.wrap(rawBehandlingId);
            var uuid = new UUID(bb.getLong(), bb.getLong());
            var behandlingId = BehandlingId.fromUUID(uuid);

            var rawCount  = (Number) row[1];
            var count  = rawCount == null ? 0 : rawCount.intValue();
            result.put(behandlingId, count);
        }
        return result;
    }

}
