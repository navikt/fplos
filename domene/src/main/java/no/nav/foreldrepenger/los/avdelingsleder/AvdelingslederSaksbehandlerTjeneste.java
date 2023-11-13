package no.nav.foreldrepenger.los.avdelingsleder;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.organisasjon.SaksbehandlerGruppe;
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
        organisasjonRepository.persistFlush(saksbehandler);
        organisasjonRepository.refresh(avdeling);
    }

    public void fjernSaksbehandlerFraAvdeling(String saksbehandlerIdent, String avdelingEnhet) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent)
            .orElseThrow(() -> AvdelingSaksbehandlerTjenesteFeil.finnerIkkeSaksbehandler(saksbehandlerIdent));
        saksbehandler.fjernAvdeling(organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow());
        organisasjonRepository.persistFlush(saksbehandler);

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
        organisasjonRepository.persistFlush(saksbehandler);
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident)
            .orElseThrow(() -> AvdelingSaksbehandlerTjenesteFeil.finnerIkkeSaksbehandler(ident));
    }

    public List<SaksbehandlerGruppe> hentAvdelingensSaksbehandlereOgGrupper(String avdelingEnhet) {
        return organisasjonRepository.hentSaksbehandlerGrupper(avdelingEnhet);
    }

    public void leggSaksbehandlerTilGruppe(String saksbehandlerId, long gruppeId, String avdelingEnhet) {
        organisasjonRepository.leggSaksbehandlerTilGruppe(saksbehandlerId, gruppeId, avdelingEnhet);
    }

    public void fjernSaksbehandlerFraGruppe(String saksbehandlerId, long gruppeId, String avdelingEnhet) {
        organisasjonRepository.fjernSaksbehandlerFraGruppe(saksbehandlerId, gruppeId, avdelingEnhet);
    }

    public SaksbehandlerGruppe opprettSaksbehandlerGruppe(String avdelingEnhet) {
        var gruppe = new SaksbehandlerGruppe("Ny saksbehandlergruppe");
        var avdeling = hentAvdeling(avdelingEnhet);
        gruppe.setAvdeling(avdeling);
        organisasjonRepository.persistFlush(gruppe);
        return gruppe;
    }

    public void endreSaksbehandlerGruppeNavn(long gruppeId, String gruppeNavn, String avdelingEnhet) {
        organisasjonRepository.updateSaksbehandlerGruppeNavn(gruppeId, gruppeNavn, avdelingEnhet);
    }

    public void slettSaksbehandlerGruppe(long gruppeId, String avdelingEnhet) {
        organisasjonRepository.slettSaksbehandlerGruppe(gruppeId, avdelingEnhet);
    }

    private static final class AvdelingSaksbehandlerTjenesteFeil {
        private static TekniskException finnerIkkeSaksbehandler(String ident) {
            return new TekniskException("Finner ikke saksbehandler med ident {}", ident);
        }
    }

}
