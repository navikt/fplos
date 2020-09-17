package no.nav.fplos.admin;

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
        oppgaver.forEach(oppgave -> opprettSynkroniseringTask(oppgave, mapper, callId));
        return OppgaveEgenskapOppdatererTask.TASKTYPE + "-" + oppgaver.size();
    }

    private void opprettSynkroniseringTask(Oppgave oppgave, OppgaveEgenskapTypeMapper typeMapper, String callId) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(OppgaveEgenskapOppdatererTask.TASKTYPE);
        prosessTaskData.setCallId(callId + oppgave.getId());
        prosessTaskData.setOppgaveId(String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER, typeMapper.name());
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
