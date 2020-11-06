package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@ProsessTask(OppgaveKorrigerEndretdatoTask.TASKTYPE)
public class OppgaveKorrigerEndretdatoTask implements ProsessTaskHandler {
    private static final Logger log = LoggerFactory.getLogger(OppgaveKorrigerEndretdatoTask.class);

    public static final String TASKTYPE = "oppgaveendretdato.oppdaterer";

    private EntityManager entityManager;

    @Inject
    public OppgaveKorrigerEndretdatoTask(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public OppgaveKorrigerEndretdatoTask() {
        // CDI
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var oppgaveId = Long.parseLong(prosessTaskData.getPropertyValue(ProsessTaskData.OPPGAVE_ID));
        var oppgave = hentOppgave(oppgaveId);
        var eventLogg = hentEventLogg(oppgave.getBehandlingId());

        Optional<BehandlingEventLogg> tilhørendeOppgaveEvent = eventLogg.stream().filter(o -> {
            var loggInnslagTid = o.getOpprettetTid();
            var oppgaveEndretTid = oppgave.getEndretTid();
            return oppgaveEndretTid.isAfter(loggInnslagTid.minus(500, ChronoUnit.MILLIS))
                    && oppgaveEndretTid.isBefore(loggInnslagTid.plus(500, ChronoUnit.MILLIS));
        }).findFirst();

        if (tilhørendeOppgaveEvent.isEmpty()) {
            log.info("Fant ingen tilhørende oppgaveEventLogg-innslag for oppgaveId {}", oppgave.getId());
        } else {
            var aktuelleEventer = eventLogg.stream()
                    .filter(el -> tilhørendeOppgaveEvent.get().getOpprettetTid().isAfter(el.getOpprettetTid()))
                    .collect(Collectors.toList());

            tilhørendeOppgaveEvent
                    .flatMap(t -> finnEldreDuplikatEvent(aktuelleEventer, t.getType()))
                    .map(BehandlingEventLogg::getOpprettetTid)
                    .ifPresentOrElse(korrigertTid -> {
                        log.info("OppgaveId: {}, eksisterende endret_tid: {}, korrigert endret_tid_2: {}", oppgave.getId(), oppgave.getEndretTid(), korrigertTid);
                        updateEndretTid(oppgave, korrigertTid);
                    }, () -> log.info("OppgaveId: {}. Ikke behov for å korrigere endret_tid", oppgave.getId()));
        }
    }

    private static Optional<BehandlingEventLogg> finnEldreDuplikatEvent(List<BehandlingEventLogg> eventer, String type) {
        if (eventer.size() == 0) {
            return Optional.empty();
        }
        var candidate = eventer.get(0);
        if (candidate.getType().equals(type)) {
            if (eventer.size() == 1) {
                return Optional.of(candidate);
            } else {
                var nextCandidate = finnEldreDuplikatEvent(eventer.subList(1, eventer.size()), type);
                return nextCandidate.isPresent() ? nextCandidate : Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    private List<BehandlingEventLogg> hentEventLogg(byte[] behandlingId) {
        Query query = entityManager.createNativeQuery("select opprettet_tid, EVENT_TYPE from OPPGAVE_EVENT_LOGG where behandling_id = :behandlingId")
                .setParameter("behandlingId", behandlingId);
        List<Object[]> queryResultat = (List<Object[]>) query.getResultList();
        return queryResultat.stream()
                .map(o -> {
                    var opprettetTid = ((Timestamp) o[0]).toLocalDateTime();
                    var type = (String) o[1];
                    return new BehandlingEventLogg(opprettetTid, type);
                })
                .sorted(Comparator.comparing(BehandlingEventLogg::getOpprettetTid).reversed())
                .collect(Collectors.toList());

    }

    private void updateEndretTid(Oppgave oppgave, LocalDateTime korrigertTid) {
        // TODO: både endret_tid og avsluttet_tid bør kanskje oppdateres
        entityManager.createNativeQuery("update oppgave set endret_TID_2 = :korrigertTid where id = :oppgaveId")
                .setParameter("korrigertTid", korrigertTid)
                .setParameter("oppgaveId", oppgave.getId())
                .executeUpdate();
    }

    private Oppgave hentOppgave(long oppgaveId) {
        Query query = entityManager.createNativeQuery("select ID, ENDRET_TID, behandling_id from OPPGAVE where ID = :oppgaveId")
                .setParameter("oppgaveId", oppgaveId);
        Object[] queryResultat = (Object[]) query.getSingleResult();
        var id = ((BigDecimal) queryResultat[0]).longValue();
        var endretTid = ((Timestamp) queryResultat[1]).toLocalDateTime();

        var behandlingIdBytes = ((byte[]) queryResultat[2]);
        ByteBuffer bb = ByteBuffer.wrap(behandlingIdBytes);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        var behandlingId = BehandlingId.fromUUID(uuid);

        return new Oppgave(id, endretTid, behandlingIdBytes);
    }

    private class Oppgave {
        private long id;
        private LocalDateTime endretTid;
        private byte[] behandlingId;

        public Oppgave(long id, LocalDateTime endretTid, byte[] behandlingId) {
            this.id = id;
            this.endretTid = endretTid;
            this.behandlingId = behandlingId;
        }

        public long getId() {
            return id;
        }

        public LocalDateTime getEndretTid() {
            return endretTid;
        }

        public byte[] getBehandlingId() {
            return behandlingId;
        }

        public void setEndretTid(LocalDateTime endretTid) {
            this.endretTid = endretTid;
        }
    }

    private class BehandlingEventLogg {

        private final LocalDateTime opprettetTid;
        private final String type;

        public BehandlingEventLogg(LocalDateTime opprettetTid, String type) {
            this.opprettetTid = opprettetTid;
            this.type = type;
        }

        public LocalDateTime getOpprettetTid() {
            return opprettetTid;
        }

        public String getType() {
            return type;
        }
    }
}
