package no.nav.foreldrepenger.los.oppgavekø;

import static no.nav.foreldrepenger.los.felles.util.BrukerIdent.brukerIdent;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.Oppgavespørring;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.vedtak.exception.FunksjonellException;

@ApplicationScoped
public class OppgaveKøTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveKøTjeneste.class);

    private OppgaveRepository oppgaveRepository;
    private OrganisasjonRepository organisasjonRepository;

    @Inject
    public OppgaveKøTjeneste(OppgaveRepository oppgaveRepository, OrganisasjonRepository organisasjonRepository) {
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
        var avdelingId = organisasjonRepository.hentAvdelingFraEnhet(enhet)
            .map(Avdeling::getId)
            .orElseThrow(() -> new IllegalStateException("Finner ikke avdeling fra enhet " + enhet + ". OppgaveId " + oppgave.getId()));
        var potensielleKøer = oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetAvdeling(avdelingId);
        return potensielleKøer.stream().map(pk -> finnOppgaveFiltreringKnytning(oppgave, pk)).filter(Optional::isPresent).map(Optional::get).toList();
    }

    public Integer hentAntallOppgaver(Long behandlingsKø, boolean forAvdelingsleder) {
        var queryDto = oppgaveRepository.hentOppgaveFilterSett(behandlingsKø)
            .map(Oppgavespørring::new)
            .orElseThrow(() -> new FunksjonellException("FP-164687", "Fant ikke oppgavekø med id " + behandlingsKø));
        queryDto.setForAvdelingsleder(forAvdelingsleder);
        return oppgaveRepository.hentAntallOppgaver(queryDto);
    }

    public Integer hentAntallOppgaverForAvdeling(String avdelingsEnhet) {
        var avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet).orElseThrow();
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
        oppgavespørring.setIgnorerReserversjoner(true);
        var antall = oppgaveRepository.hentAntallOppgaver(oppgavespørring);
        LOG.debug("Sjekker om oppgave {} tilfredstiller filtrering {}. Spørring {}. Resultat {}", oppgave.getId(), oppgaveFiltrering.getId(),
            oppgavespørring, antall > 0);
        return antall > 0;
    }
}
