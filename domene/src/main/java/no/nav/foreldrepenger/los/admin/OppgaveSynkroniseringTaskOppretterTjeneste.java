package no.nav.foreldrepenger.los.admin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.log.mdc.MDCOperations;

@ApplicationScoped
public class OppgaveSynkroniseringTaskOppretterTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(OppgaveSynkroniseringTaskOppretterTjeneste.class);
    private OppgaveRepository oppgaveRepository;
    private ProsessTaskTjeneste prosessTaskTjeneste;

    OppgaveSynkroniseringTaskOppretterTjeneste() {
        // for CDI proxy
    }

    @Inject
    public OppgaveSynkroniseringTaskOppretterTjeneste(OppgaveRepository oppgaveRepository,
                                                      ProsessTaskTjeneste prosessTaskTjeneste) {
        this.oppgaveRepository = oppgaveRepository;
        this.prosessTaskTjeneste = prosessTaskTjeneste;
    }

    public int opprettOppgaveEgenskapOppdatererTask(String kriterieType) {
        final var callId = (MDCOperations.getCallId() == null ? MDCOperations.generateCallId() : MDCOperations.getCallId()) + "_";
        var mapper = Optional.of(AndreKriterierType.fraKode(kriterieType))
                .flatMap(OppgaveEgenskapTypeMapper::tilTypeMapper)
                .orElseThrow();
        var oppgaver = oppgaveRepository.hentOppgaverForSynkronisering();
        LOG.info("Oppretter tasker for synkronisering av oppgaveegenskap {} for {} oppgaver", mapper.getType(), oppgaver.size());
        var kjøres = LocalDateTime.now();
        for (var oppgave : oppgaver) {
            opprettSynkroniseringTask(oppgave, mapper, callId, kjøres);
            kjøres = kjøres.plus(500, ChronoUnit.MILLIS);
        }
        return oppgaver.size();
    }

    private void opprettSynkroniseringTask(Oppgave oppgave, OppgaveEgenskapTypeMapper typeMapper, String callId, LocalDateTime kjøretidspunkt) {
        var prosessTaskData = ProsessTaskData.forProsessTask(OppgaveEgenskapOppdatererTask.class);
        prosessTaskData.setCallId(callId + oppgave.getId());
        prosessTaskData.setPrioritet(999);
        prosessTaskData.setNesteKjøringEtter(kjøretidspunkt);
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.OPPGAVE_ID_TASK_KEY, String.valueOf(oppgave.getId()));
        prosessTaskData.setProperty(OppgaveEgenskapOppdatererTask.EGENSKAPMAPPER_TASK_KEY, typeMapper.name());
        prosessTaskTjeneste.lagre(prosessTaskData);
    }
}
