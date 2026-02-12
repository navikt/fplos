package no.nav.foreldrepenger.los.tjenester.admin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@Dependent
@ProsessTask(value = "vedlikehold.oppdater.saksbehandler", maxFailedRuns = 1)
public class OppdaterSaksbehandlerTask implements ProsessTaskHandler {

    public static final String NOREPEAT = "norepeat";
    private static final String IDENT = "ident";
    private static final String SISTE = "SISTEN";

    private static final Logger LOG = LoggerFactory.getLogger(OppdaterSaksbehandlerTask.class);
    private final OrganisasjonRepository organisasjonsRepository;
    private final AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;
    private final ProsessTaskTjeneste prosessTaskTjeneste;


    @Inject
    public OppdaterSaksbehandlerTask(OrganisasjonRepository organisasjonRepository,
                                     AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste,
                                     ProsessTaskTjeneste prosessTaskTjeneste) {
        this.organisasjonsRepository = organisasjonRepository;
        this.avdelingslederSaksbehandlerTjeneste = avdelingslederSaksbehandlerTjeneste;
        this.prosessTaskTjeneste = prosessTaskTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var oppgittIdent = Optional.ofNullable(prosessTaskData.getPropertyValue(IDENT));
        var repeat = Optional.ofNullable(prosessTaskData.getPropertyValue(NOREPEAT)).filter(NOREPEAT::equals).isEmpty();
        if (oppgittIdent.isEmpty()) {
            lagOppdaterTasksForAlleSaksbehandlere();
        } else if (oppgittIdent.filter(SISTE::equals).isPresent()) {
            ryddeAvgåttePlanleggNesteKjøring(repeat);
        } else {
            var saksbehandler = avdelingslederSaksbehandlerTjeneste.oppdaterSaksbehandler(oppgittIdent.orElseThrow());
            var postfiks = saksbehandler.getNavn() != null ? "fortsatt" : "ikke";
            LOG.info("Oppdater saksbehandler {} finnes {}.", saksbehandler.getSaksbehandlerIdent(), postfiks);
        }
    }

    private void ryddeAvgåttePlanleggNesteKjøring(boolean repeat) {
        var antallSlettet = organisasjonsRepository.fjernSaksbehandlereSomHarSluttet();
        LOG.info("Oppdater saksbehandler: Fjernet {} saksbehandlere som har sluttet.", antallSlettet);
        if (repeat) {
            var nesteKjøring = LocalDate.now().plusWeeks(4).atTime(LocalTime.of(19, 0)); // Internasjonal oppetid
            var neste = ProsessTaskData.forProsessTask(OppdaterSaksbehandlerTask.class);
            neste.setNesteKjøringEtter(nesteKjøring);
            prosessTaskTjeneste.lagre(neste);
        }
    }

    private void lagOppdaterTasksForAlleSaksbehandlere() {
        var gruppe = new ProsessTaskGruppe();
        organisasjonsRepository.hentAlleSaksbehandlere().forEach(s -> {
            var t = ProsessTaskData.forProsessTask(OppdaterSaksbehandlerTask.class);
            t.setProperty(OppdaterSaksbehandlerTask.IDENT, s.getSaksbehandlerIdent());
            gruppe.addNesteSekvensiell(t);
        });
        var siste = ProsessTaskData.forProsessTask(OppdaterSaksbehandlerTask.class);
        siste.setProperty(OppdaterSaksbehandlerTask.IDENT, SISTE);
        gruppe.addNesteSekvensiell(siste);
        prosessTaskTjeneste.lagre(gruppe);
    }
}
