package no.nav.foreldrepenger.los.avdelingsleder;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class AvdelingslederSaksbehandlerTjeneste {

    private OrganisasjonRepository organisasjonRepository;
    private OppgaveRepository oppgaveRepository;

    AvdelingslederSaksbehandlerTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederSaksbehandlerTjeneste(OppgaveRepository oppgaveRepository, OrganisasjonRepository organisasjonRepository) {
        this.organisasjonRepository = organisasjonRepository;
        this.oppgaveRepository = oppgaveRepository;
    }

    public List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet) {
        return organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).map(Avdeling::getSaksbehandlere).orElse(Collections.emptyList());
    }

    public void leggSaksbehandlerTilAvdeling(String saksbehandlerIdent, String avdelingEnhet) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent)
            .orElseGet(() -> opprettSaksbehandler(saksbehandlerIdent));
        var avdeling = hentAvdeling(avdelingEnhet);
        saksbehandler.leggTilAvdeling(avdeling);
        organisasjonRepository.lagre(saksbehandler);
        organisasjonRepository.refresh(avdeling);
    }

    public void fjernSaksbehandlerFraAvdeling(String saksbehandlerIdent, String avdelingEnhet) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent)
            .orElseThrow(() -> AvdelingSaksbehandlerTjenesteFeil.finnerIkkeSaksbehandler(saksbehandlerIdent));
        saksbehandler.fjernAvdeling(organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow());
        organisasjonRepository.lagre(saksbehandler);

        var avdeling = hentAvdeling(avdelingEnhet);
        var oppgaveFiltreringList = avdeling.getOppgaveFiltrering();
        for (var oppgaveFiltrering : oppgaveFiltreringList) {
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
