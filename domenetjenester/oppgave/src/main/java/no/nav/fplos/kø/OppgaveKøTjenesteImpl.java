package no.nav.fplos.kø;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgavekø.Oppgavekø;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.Oppgavespørring;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.oppgave.OppgaveTjenesteFeil;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;

@ApplicationScoped
public class OppgaveKøTjenesteImpl implements OppgaveKøTjeneste {

    private OppgaveRepository oppgaveRepository;
    private OrganisasjonRepository organisasjonRepository;

    @Inject
    public OppgaveKøTjenesteImpl(OppgaveRepository oppgaveRepository,
                                 OrganisasjonRepository organisasjonRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.organisasjonRepository = organisasjonRepository;
    }

    OppgaveKøTjenesteImpl() {
    }

    @Override
    public List<OppgaveFiltrering> hentAlleOppgaveFiltrering(String brukerIdent) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(brukerIdent)
                .map(Saksbehandler::getOppgaveFiltreringer)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<OppgaveFiltrering> hentOppgaveFiltreringerForPåloggetBruker() {
        return hentAlleOppgaveFiltrering(finnBrukernavn());
    }

    @Override
    public List<Oppgavekø> finnKøerSomInneholder(Oppgave oppgave) {
        var enhet = oppgave.getBehandlendeEnhet();
        //var potensielleKøer = oppgaveRepository.hentAlleOppgaveFiltreringsettTilknyttetAvdeling(enhet);
        return Collections.emptyList();
        //return potensielleKøer.stream().map(pk -> new Oppgavekø(pk.getId())).collect(Collectors.toList());
    }


    @Override
    public Integer hentAntallOppgaver(Long behandlingsKø, boolean forAvdelingsleder) {
        var queryDto = oppgaveRepository.hentFiltrering(behandlingsKø)
                .map(Oppgavespørring::new)
                .orElseThrow(() -> OppgaveTjenesteFeil.FACTORY.fantIkkeOppgavekø(behandlingsKø).toException());
        queryDto.setForAvdelingsleder(forAvdelingsleder);
        return oppgaveRepository.hentAntallOppgaver(queryDto);
    }

    @Override
    public Integer hentAntallOppgaverForAvdeling(String avdelingsEnhet) {
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet).orElseThrow();
        return oppgaveRepository.hentAntallOppgaverForAvdeling(avdeling.getId());
    }


    @Override
    public List<Oppgave> hentOppgaver(Long sakslisteId) {
        return hentOppgaver(sakslisteId, 0);
    }

    @Override
    public List<Oppgave> hentOppgaver(Long sakslisteId, int maksAntall) {
        return oppgaveRepository.hentFiltrering(sakslisteId)
                .map(Oppgavespørring::new)
                .map(os -> oppgaveRepository.hentOppgaver(os, maksAntall))
                .orElse(Collections.emptyList());
    }

    private static String finnBrukernavn() {
        String brukerident = SubjectHandler.getSubjectHandler().getUid();
        return brukerident != null ? brukerident.toUpperCase() : BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }
}
