package no.nav.foreldrepenger.los.admin;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandling;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask("oppgaveegenskap.oppdaterer")
public class OppgaveEgenskapOppdatererTask implements ProsessTaskHandler {

    public static final String EGENSKAPMAPPER_TASK_KEY = "oppgaveegenskap.egenskapmapper";
    public static final String OPPGAVE_ID_TASK_KEY = "oppgaveId";

    private OppgaveRepository oppgaveRepository;
    private ForeldrepengerBehandling fpsakKlient;

    @Inject
    public OppgaveEgenskapOppdatererTask(OppgaveRepository oppgaveRepository, @Jersey ForeldrepengerBehandling fpsakKlient) {
        this.oppgaveRepository = oppgaveRepository;
        this.fpsakKlient = fpsakKlient;
    }

    public OppgaveEgenskapOppdatererTask() {
        // CDI
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var oppgaveId = Long.parseLong(prosessTaskData.getPropertyValue(OPPGAVE_ID_TASK_KEY));
        var egenskapMapper = OppgaveEgenskapTypeMapper.valueOf(prosessTaskData.getPropertyValue(EGENSKAPMAPPER_TASK_KEY));
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
