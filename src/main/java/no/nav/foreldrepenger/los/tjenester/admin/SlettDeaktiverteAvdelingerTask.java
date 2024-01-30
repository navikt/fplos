package no.nav.foreldrepenger.los.tjenester.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.SaksbehandlerGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@Dependent
@ProsessTask(value = "vedlikehold.slettavdeling", maxFailedRuns = 1)
public class SlettDeaktiverteAvdelingerTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SlettDeaktiverteAvdelingerTask.class);
    private final OrganisasjonRepository organisasjonsRepository;
    private final AvdelingslederTjeneste avdelingslederTjeneste;
    private final AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;
    private OppgaveRepository oppgaveRepository;

    @Inject
    public SlettDeaktiverteAvdelingerTask(OppgaveRepository oppgaveRepository,
                                          OrganisasjonRepository organisasjonRepository,
                                          AvdelingslederTjeneste avdelingslederTjeneste,
                                          AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste) {
        this.oppgaveRepository = oppgaveRepository;
        this.organisasjonsRepository = organisasjonRepository;
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.avdelingslederSaksbehandlerTjeneste = avdelingslederSaksbehandlerTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var enhetsnummer = prosessTaskData.getPropertyValue("enhetsnummer");

        var avdeling = organisasjonsRepository.hentAvdelingFraEnhet(enhetsnummer).orElse(null);
        if (avdeling == null || avdeling.getErAktiv()) {
            LOG.info("Fant ikke inaktiv enhet {}, avslutter.", enhetsnummer);
            return;
        }

        var antallÅpneOppgaver = oppgaveRepository.hentAntallOppgaverForAvdeling(avdeling.getId());
        if (antallÅpneOppgaver > 0) {
            LOG.warn("Fant {} aktive oppgaver tilknyttet enhetsnummer {}, avbryter avdelingssletting.", antallÅpneOppgaver, enhetsnummer);
            return;
        }

        var grupper = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlereOgGrupper(enhetsnummer)
            .stream().map(SaksbehandlerGruppe::getId).toList();
        LOG.info("Sletter {} saksbehandlergrupper tilknyttet enhet {}", grupper.size(), enhetsnummer);
        grupper.forEach(g -> avdelingslederSaksbehandlerTjeneste.slettSaksbehandlerGruppe(g, enhetsnummer));

        var køer = avdeling.getOppgaveFiltrering().stream().map(OppgaveFiltrering::getId).toList();
        LOG.info("Sletter {} køer tilknyttet enhet {}", køer.size(), enhetsnummer);
        køer.forEach(avdelingslederTjeneste::slettOppgaveFiltrering);

        organisasjonsRepository.slettAvdeling(avdeling);
        LOG.info("Slettet enhet {}", enhetsnummer);
    }
}
