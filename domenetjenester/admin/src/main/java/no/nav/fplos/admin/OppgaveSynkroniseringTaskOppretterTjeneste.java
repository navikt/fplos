package no.nav.fplos.admin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.log.mdc.MDCOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;

@ApplicationScoped
public class OppgaveSynkroniseringTaskOppretterTjeneste {
    private static final Logger log = LoggerFactory.getLogger(OppgaveSynkroniseringTaskOppretterTjeneste.class);
    private OppgaveRepository oppgaveRepository;
    private ProsessTaskRepository prosessTaskRepository;

    OppgaveSynkroniseringTaskOppretterTjeneste() {
        // for CDI proxy
    }

    @Inject
    public OppgaveSynkroniseringTaskOppretterTjeneste(OppgaveRepository oppgaveRepository,
                                                      ProsessTaskRepository prosessTaskRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.prosessTaskRepository = prosessTaskRepository;
    }

    public String opprettOppgaveEgenskapOppdatererTask(String kriterieType) {
        final String callId = (MDCOperations.getCallId() == null ? MDCOperations.generateCallId() : MDCOperations.getCallId()) + "_";
        var mapper = Optional.of(AndreKriterierType.fraKode(kriterieType))
                .flatMap(OppgaveEgenskapTypeMapper::tilTypeMapper)
                .orElseThrow();
        var oppgaver = oppgaveRepository.hentOppgaverForSynkronisering();
        log.info("Oppretter tasker for synkronisering av oppgaveegenskap {} for {} oppgaver", mapper.getType(), oppgaver.size());
        var kjøres = LocalDateTime.now();
        for (var oppgave : oppgaver) {
            opprettSynkroniseringTask(oppgave, mapper, callId, kjøres);
            kjøres = kjøres.plus(500, ChronoUnit.MILLIS);
        }
        return OppgaveEgenskapOppdatererTask.TASKTYPE + "-" + oppgaver.size();
    }

    private void opprettSynkroniseringTask(Oppgave oppgave, OppgaveEgenskapTypeMapper typeMapper, String callId, LocalDateTime kjøretidspunkt) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setCallId(callId + oppgave.getId());
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setPrioritet(999);
        prosessTaskData.setNesteKjøringEtter(kjøretidspunkt);
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, typeMapper.name());
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
