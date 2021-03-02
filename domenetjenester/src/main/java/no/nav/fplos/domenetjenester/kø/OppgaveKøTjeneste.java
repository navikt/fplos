package no.nav.fplos.domenetjenester.kø;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgavekø.OppgaveFiltreringKnytning;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.Oppgavespørring;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.domenetjenester.oppgave.OppgaveTjenesteFeil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.nav.fplos.domenetjenester.util.BrukerIdent.brukerIdent;

@ApplicationScoped
public class OppgaveKøTjeneste {

    private OppgaveRepository oppgaveRepository;
    private OrganisasjonRepository organisasjonRepository;

    @Inject
    public OppgaveKøTjeneste(OppgaveRepository oppgaveRepository,
                             OrganisasjonRepository organisasjonRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.organisasjonRepository = organisasjonRepository;
    }

    OppgaveKøTjeneste() {
    }

    public List<OppgaveFiltrering> hentAlleOppgaveFiltrering(String brukerIdent) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(brukerIdent)
                .map(Saksbehandler::getOppgaveFiltreringer)
                .orElse(Collections.emptyList());
    }

    public List<OppgaveFiltrering> hentOppgaveFiltreringerForPåloggetBruker() {
        return hentAlleOppgaveFiltrering(brukerIdent());
    }

    public List<OppgaveFiltreringKnytning> finnOppgaveFiltreringKnytninger(Oppgave oppgave) {
        var enhet = oppgave.getBehandlendeEnhet();
        var orgnr = organisasjonRepository.hentAvdelingFraEnhet(enhet)
                .map(Avdeling::getId)
                .orElseThrow();
        var potensielleKøer = oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(orgnr);
        return potensielleKøer.stream()
                .map(pk -> finnOppgaveFiltreringKnytning(oppgave, pk))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

    public Integer hentAntallOppgaver(Long behandlingsKø, boolean forAvdelingsleder) {
        var queryDto = oppgaveRepository.hentOppgaveFilterSett(behandlingsKø)
                .map(Oppgavespørring::new)
                .orElseThrow(() -> OppgaveTjenesteFeil.FACTORY.fantIkkeOppgavekø(behandlingsKø).toException());
        queryDto.setForAvdelingsleder(forAvdelingsleder);
        return oppgaveRepository.hentAntallOppgaver(queryDto);
    }

    public Integer hentAntallOppgaverForAvdeling(String avdelingsEnhet) {
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet).orElseThrow();
        return oppgaveRepository.hentAntallOppgaverForAvdeling(avdeling.getId());
    }

    public List<Oppgave> hentOppgaver(Long sakslisteId) {
        return hentOppgaver(sakslisteId, 0);
    }

    public List<Oppgave> hentOppgaver(Long sakslisteId, int maksAntall) {
        return oppgaveRepository.hentOppgaveFilterSett(sakslisteId)
                .map(Oppgavespørring::new)
                .map(os -> oppgaveRepository.hentOppgaver(os, maksAntall))
                .orElse(Collections.emptyList());
    }

    private Optional<OppgaveFiltreringKnytning> finnOppgaveFiltreringKnytning(Oppgave oppgave, OppgaveFiltrering oppgaveFiltrering) {
        if (oppgaveTilfredstillerOppgaveFiltreringSett(oppgave, oppgaveFiltrering)) {
            var knytning = new OppgaveFiltreringKnytning(oppgave.getId(), oppgaveFiltrering.getId(), oppgave.getBehandlingType());
            return Optional.of(knytning);
        }
        return Optional.empty();
    }

    private boolean oppgaveTilfredstillerOppgaveFiltreringSett(Oppgave oppgave, OppgaveFiltrering oppgaveFiltrering) {
        var oppgavespørring = new Oppgavespørring(oppgaveFiltrering);
        oppgavespørring.setAvgrensTilOppgaveId(oppgave.getId());
        return oppgaveRepository.hentOppgaver(oppgavespørring).size() > 0;
    }
}
