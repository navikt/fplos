package no.nav.fplos.domenetjenester.avdelingsleder;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class AvdelingslederSaksbehandlerTjenesteImpl implements AvdelingslederSaksbehandlerTjeneste {

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
        return organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet)
                .map(Avdeling::getSaksbehandlere)
                .orElse(Collections.emptyList());
    }

    @Override
    public void leggSaksbehandlerTilAvdeling(String saksbehandlerIdent, String avdelingEnhet) {
        Saksbehandler saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent)
                .orElseGet(() -> opprettSaksbehandler(saksbehandlerIdent));
        Avdeling avdeling = hentAvdeling(avdelingEnhet);
        saksbehandler.leggTilAvdeling(avdeling);
        organisasjonRepository.lagre(saksbehandler);
        organisasjonRepository.refresh(avdeling);
    }

    @Override
    public void fjernSaksbehandlerFraAvdeling(String saksbehandlerIdent, String avdelingEnhet) {
        Saksbehandler saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent)
                .orElseThrow(() -> AvdelingSaksbehandlerTjenesteFeil.finnerIkkeSaksbehandler(saksbehandlerIdent));
        saksbehandler.fjernAvdeling(organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow());
        organisasjonRepository.lagre(saksbehandler);

        Avdeling avdeling = hentAvdeling(avdelingEnhet);
        List<OppgaveFiltrering> oppgaveFiltreringList = avdeling.getOppgaveFiltrering();
        for (OppgaveFiltrering oppgaveFiltrering : oppgaveFiltreringList) {
            oppgaveFiltrering.fjernSaksbehandler(saksbehandler);
            oppgaveRepository.lagre(oppgaveFiltrering);
        }
        oppgaveRepository.refresh(avdeling);
    }

    private Avdeling hentAvdeling(String avdelingEnhet) {
        return organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow();
    }

    private Saksbehandler opprettSaksbehandler(String ident) {
        var saksbehandler = new Saksbehandler(ident.toUpperCase());
        organisasjonRepository.lagre(saksbehandler);
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident)
                .orElseThrow(() -> AvdelingSaksbehandlerTjenesteFeil.finnerIkkeSaksbehandler(ident));
    }

    private static final class AvdelingSaksbehandlerTjenesteFeil {
        private static TekniskException finnerIkkeSaksbehandler(String ident) {
            return new TekniskException("Finner ikke saksbehandler med ident {}", ident);
        }
    }

}
