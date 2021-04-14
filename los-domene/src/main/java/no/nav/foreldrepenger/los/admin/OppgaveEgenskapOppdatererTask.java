package no.nav.foreldrepenger.los.admin;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandlingKlient;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(OppgaveEgenskapOppdatererTask.TASKTYPE)
public class OppgaveEgenskapOppdatererTask implements ProsessTaskHandler {
    public static final String TASKTYPE = "oppgaveegenskap.oppdaterer";
    public static final String EGENSKAPMAPPER = "oppgaveegenskap.egenskapmapper";

    private OppgaveRepository oppgaveRepository;
    private ForeldrepengerBehandlingKlient fpsakKlient;

    @Inject
    public OppgaveEgenskapOppdatererTask(OppgaveRepository oppgaveRepository, ForeldrepengerBehandlingKlient fpsakKlient) {
        this.oppgaveRepository = oppgaveRepository;
        this.fpsakKlient = fpsakKlient;
    }

    public OppgaveEgenskapOppdatererTask() {
        // CDI
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var oppgaveId = Long.parseLong(prosessTaskData.getPropertyValue(ProsessTaskData.OPPGAVE_ID));
        var egenskapMapper = OppgaveEgenskapTypeMapper.valueOf(prosessTaskData.getPropertyValue(EGENSKAPMAPPER));
        var oppgave = oppgaveRepository.hentOppgave(oppgaveId);
        var eksisterendeEgenskaper = oppgaveRepository.hentOppgaveEgenskaper(oppgaveId);

        var eksistererAktivEgenskap = eksistererAktivEgenskap(eksisterendeEgenskaper, egenskapMapper);
        if (oppgave.getAktiv() && !eksistererAktivEgenskap) {
            var behandling = fpsakKlient.getBehandling(oppgave.getBehandlingId());
            if (egenskapMapper.erEgenskapAktuell(behandling)) {
                var aktivEgenskap = lagAktivEgenskap(oppgave, eksisterendeEgenskaper, egenskapMapper);
                oppgaveRepository.lagre(aktivEgenskap);
            }
        }
    }

    private static OppgaveEgenskap lagAktivEgenskap(Oppgave oppgave, List<OppgaveEgenskap> eksisterendeEgenskaper,
                                                    OppgaveEgenskapTypeMapper oppgaveEgenskapTypeMapper) {
        var type = oppgaveEgenskapTypeMapper.getType();
        var egenskap = eksisterendeEgenskaper.stream()
                .filter(oe -> oe.getAndreKriterierType().equals(type))
                .findAny()
                .orElseGet(() -> new OppgaveEgenskap(oppgave, type));
        egenskap.aktiverOppgaveEgenskap();
        return egenskap;
    }

    private static boolean eksistererAktivEgenskap(List<OppgaveEgenskap> oppgaver, OppgaveEgenskapTypeMapper type) {
        return oppgaver.stream()
                .filter(OppgaveEgenskap::getAktiv)
                .map(OppgaveEgenskap::getAndreKriterierType)
                .anyMatch(k -> k.equals(type.getType()));
    }
}
