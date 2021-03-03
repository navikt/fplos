package no.nav.foreldrepenger.los.admin;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.log.mdc.MDCOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OppgaveKorrigerEndretdatoTaskOppretterTjeneste {
    private static final Logger log = LoggerFactory.getLogger(OppgaveKorrigerEndretdatoTaskOppretterTjeneste.class);
    private ProsessTaskRepository prosessTaskRepository;
    private EntityManager entityManager;

    OppgaveKorrigerEndretdatoTaskOppretterTjeneste() {
        // for CDI proxy
    }

    @Inject
    public OppgaveKorrigerEndretdatoTaskOppretterTjeneste(ProsessTaskRepository prosessTaskRepository,
                                                          EntityManager entityManager) {
        this.prosessTaskRepository = prosessTaskRepository;
        this.entityManager = entityManager;
    }

    public String opprettOppgaveEgenskapOppdatererTask(int antallOppgavver) {
        final String callId = (MDCOperations.getCallId() == null ? MDCOperations.generateCallId() : MDCOperations.getCallId()) + "_";
        var oppgaver = hentOppgaveIdListe();
        log.info("Trakk ut {} oppgaver, oppretter tasker for {} ", oppgaver.size(), antallOppgavver);
        var kjøres = LocalDateTime.now();
        oppgaver.stream().limit(antallOppgavver).forEach(o -> opprettTask(o.longValue(), callId, kjøres));
        return OppgaveEgenskapOppdatererTask.TASKTYPE + "-" + oppgaver.size() + "-" + Math.min(antallOppgavver, oppgaver.size());
    }

    public String settEndretTid2TilNull() {
        var antallRader = entityManager.createNativeQuery("update oppgave set endret_tid_2=null " +
                "where endret_tid_2 is not null")
                .executeUpdate();
        return String.valueOf(antallRader);
    }

    private void opprettTask(Long oppgave, String callId, LocalDateTime kjøretidspunkt) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveKorrigerEndretdatoTask.TASKTYPE);
        prosessTaskData.setCallId(callId + oppgave);
        prosessTaskData.setOppgaveId(String.valueOf(oppgave));
        prosessTaskData.setPrioritet(999);
        prosessTaskData.setNesteKjøringEtter(kjøretidspunkt);
        prosessTaskRepository.lagre(prosessTaskData);
    }

    private List<BigDecimal> hentOppgaveIdListe() {
        LocalDateTime endretFraogmed = LocalDateTime.of(2020, 11, 5, 0, 0);
        LocalDateTime endretTilogmed = LocalDateTime.of(2020, 11, 5, 15, 45);

        return (List<BigDecimal>) entityManager.createNativeQuery("select id from oppgave " +
                "where endret_tid >= :fom and endret_tid <= :tom " +
                "and endret_tid_2 is NULL")
                .setParameter("fom", sql(endretFraogmed))
                .setParameter("tom", sql(endretTilogmed))
                .getResultList();
    }

    private static Timestamp sql(LocalDateTime datetime) {
        return Timestamp.valueOf(datetime);
    }
}
