package no.nav.fplos.admin;

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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    public String opprettOppgaveEgenskapOppdatererTask() {
        final String callId = (MDCOperations.getCallId() == null ? MDCOperations.generateCallId() : MDCOperations.getCallId()) + "_";

        List<BigDecimal> oppgaver = hentOppgaveIdListe();
        log.info("Oppretter tasker for korrigering av endretDato for {} oppgaver", oppgaver.size());
        var kjøres = LocalDateTime.now();
        for (var oppgave : oppgaver) {
            opprettTask(oppgave.longValue(), callId, kjøres);
            kjøres = kjøres.plus(100, ChronoUnit.MILLIS);
        }
        return OppgaveEgenskapOppdatererTask.TASKTYPE + "-" + oppgaver.size();
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
        LocalDateTime endretTilogmed = LocalDateTime.of(2020, 11, 5, 15, 0);

        return (List<BigDecimal>) entityManager.createNativeQuery("select id from oppgave where endret_tid >= :fom and endret_tid <= :tom")
                .setParameter("fom", sql(endretFraogmed))
                .setParameter("tom", sql(endretTilogmed))
                .getResultList();
    }

    private static Timestamp sql(LocalDateTime datetime) {
        return Timestamp.valueOf(datetime);
    }
}
