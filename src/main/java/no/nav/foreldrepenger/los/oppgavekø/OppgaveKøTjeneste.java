package no.nav.foreldrepenger.los.oppgavekø;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.oppgave.Filtreringstype;
import no.nav.foreldrepenger.los.oppgave.OppgaveKøRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.Oppgavespørring;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.vedtak.exception.FunksjonellException;

@ApplicationScoped
public class OppgaveKøTjeneste {

    private OppgaveRepository oppgaveRepository;
    private OppgaveKøRepository oppgaveKøRepository;
    private OrganisasjonRepository organisasjonRepository;

    @Inject
    public OppgaveKøTjeneste(OppgaveRepository oppgaveRepository,
                             OppgaveKøRepository oppgaveKøRepository,
                             OrganisasjonRepository organisasjonRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveKøRepository = oppgaveKøRepository;
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

    public Integer hentAntallOppgaver(Long behandlingsKø, Filtreringstype filtreringstype) {
        var queryDto = oppgaveRepository.hentOppgaveFilterSett(behandlingsKø)
            .map(of -> new Oppgavespørring(of, filtreringstype))
            .orElseThrow(() -> new FunksjonellException("FP-164687", "Fant ikke oppgavekø med id " + behandlingsKø));
        return oppgaveKøRepository.hentAntallOppgaver(queryDto);
    }

    public Integer hentAntallOppgaverForAvdeling(String avdelingsEnhet) {
        var avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet).orElseThrow();
        return oppgaveKøRepository.hentAntallOppgaverForAvdeling(avdeling.getAvdelingEnhet());
    }

    public List<Oppgave> hentOppgaver(Long sakslisteId, int maksAntall) {
        var oppgaveFilter = oppgaveRepository.hentOppgaveFilterSett(sakslisteId);
        if (oppgaveFilter.isEmpty()) {
            return Collections.emptyList();
        }
        var oppgavespørring = new Oppgavespørring(oppgaveFilter.get(), Filtreringstype.LEDIGE);
        oppgavespørring.setMaksAntall(maksAntall);
        return oppgaveKøRepository.hentOppgaver(oppgavespørring);
    }

    public int hentAntallSaksbehandlere(Long sakslisteId) {
        return oppgaveRepository.hentOppgaveFilterSett(sakslisteId)
            .map(OppgaveFiltrering::getSaksbehandlere)
            .map(Collection::size).orElse(0);
    }

}
