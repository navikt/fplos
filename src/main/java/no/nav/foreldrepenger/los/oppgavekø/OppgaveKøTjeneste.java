package no.nav.foreldrepenger.los.oppgavekø;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.Oppgavespørring;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
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
        return hentAlleOppgaveFiltrering(BrukerIdent.brukerIdent());
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

    public List<Oppgave> hentOppgaver(Long sakslisteId, int maksAntall) {
        var oppgaveFilter = oppgaveRepository.hentOppgaveFilterSett(sakslisteId);
        if (oppgaveFilter.isEmpty()) {
            return Collections.emptyList();
        }
        var oppgavespørring = new Oppgavespørring(oppgaveFilter.get());
        oppgavespørring.setMaksAntall(maksAntall);
        return oppgaveRepository.hentOppgaver(oppgavespørring);
    }

}
