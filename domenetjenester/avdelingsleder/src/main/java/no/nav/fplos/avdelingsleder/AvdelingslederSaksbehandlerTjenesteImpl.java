package no.nav.fplos.avdelingsleder;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;

@ApplicationScoped
public class AvdelingslederSaksbehandlerTjenesteImpl implements AvdelingslederSaksbehandlerTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AvdelingslederSaksbehandlerTjeneste.class);

    private OrganisasjonRepository organisasjonRepository;
    private OppgaveRepository oppgaveRepository;

    AvdelingslederSaksbehandlerTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederSaksbehandlerTjenesteImpl(OppgaveRepository oppgaveRepository, OrganisasjonRepository organisasjonRepository) {
        this.organisasjonRepository = organisasjonRepository;
        this.oppgaveRepository = oppgaveRepository;
    }

    @Override
    public List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet) {
        return organisasjonRepository.hentAvdelingensSaksbehandlere(avdelingEnhet);
    }

    @Override
    public void leggTilSaksbehandler(String saksbehandlerIdent, String avdelingEnhet) {
        Saksbehandler saksbehandler = hentEllerLagreSaksbehandler(saksbehandlerIdent);
        Avdeling avdeling = hentAvdeling(avdelingEnhet);
        saksbehandler.leggTilAvdeling(avdeling);
        organisasjonRepository.lagre(saksbehandler);
        organisasjonRepository.refresh(avdeling);
    }

    private Avdeling hentAvdeling(String avdelingEnhet) {
        return organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet);
    }

    private Saksbehandler hentEllerLagreSaksbehandler(String saksbehandlerIdent) {
        Optional<Saksbehandler> optionalSaksbehandler = organisasjonRepository.hentMuligSaksbehandler(saksbehandlerIdent);
        if (optionalSaksbehandler.isEmpty()) {
            organisasjonRepository.lagre(saksbehandlerFra(saksbehandlerIdent));
            return organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        }
        return optionalSaksbehandler.get();
    }

    @Override
    public void slettSaksbehandler(String saksbehandlerIdent, String avdelingEnhet) {
        Saksbehandler saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        saksbehandler.fjernAvdeling(organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet));
        organisasjonRepository.lagre(saksbehandler);

        Avdeling avdeling = hentAvdeling(avdelingEnhet);
        List<OppgaveFiltrering> oppgaveFiltreringList = avdeling.getOppgaveFiltrering();
        for (OppgaveFiltrering oppgaveFiltrering : oppgaveFiltreringList) {
            oppgaveFiltrering.fjernSaksbehandler(saksbehandler);
            oppgaveRepository.lagre(oppgaveFiltrering);
        }
        oppgaveRepository.refresh(avdeling);
    }

    private Saksbehandler saksbehandlerFra(String saksbehandlerIdent) {
        return new Saksbehandler(saksbehandlerIdent.toUpperCase());
    }
}
