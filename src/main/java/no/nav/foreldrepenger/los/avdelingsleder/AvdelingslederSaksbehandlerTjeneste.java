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
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class AvdelingslederSaksbehandlerTjeneste {

    private OrganisasjonRepository organisasjonRepository;
    private OppgaveRepository oppgaveRepository;
    private AnsattTjeneste ansattTjeneste;

    AvdelingslederSaksbehandlerTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederSaksbehandlerTjeneste(OppgaveRepository oppgaveRepository,
                                               OrganisasjonRepository organisasjonRepository,
                                               AnsattTjeneste ansattTjeneste) {
        this.organisasjonRepository = organisasjonRepository;
        this.oppgaveRepository = oppgaveRepository;
        this.ansattTjeneste = ansattTjeneste;
    }

    public List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet) {
        return organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet)
            .map(organisasjonRepository::saksbehandlereForAvdeling)
            .orElse(Collections.emptyList());
    }

    public void leggSaksbehandlerTilAvdeling(String saksbehandlerIdent, String avdelingEnhet) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent)
            .orElseGet(() -> opprettSaksbehandler(saksbehandlerIdent));
        var avdeling = hentAvdeling(avdelingEnhet);
        organisasjonRepository.persistFlush(saksbehandler);
        organisasjonRepository.tilknyttAvdelingSaksbehandler(avdeling, saksbehandler);
    }

    public void fjernSaksbehandlerFraAvdeling(String saksbehandlerIdent, String avdelingEnhet) {
        var saksbehandler = organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent)
            .orElseThrow(() -> AvdelingSaksbehandlerTjenesteFeil.finnerIkkeSaksbehandler(saksbehandlerIdent));
        organisasjonRepository.fraknyttAvdelingSaksbehandler(organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow(), saksbehandler);

        var grupperForAvdeling =  organisasjonRepository.hentSaksbehandlerGrupper(avdelingEnhet);
        var grupperForSaksbehandler = organisasjonRepository.grupperForSaksbehandler(saksbehandler);
        grupperForSaksbehandler.stream()
            .filter(grupperForAvdeling::contains)
            .forEach(g -> organisasjonRepository.fraknyttGruppeSaksbehandler(g, saksbehandler));

        var avdeling = hentAvdeling(avdelingEnhet);
        var oppgaveFiltreringList = oppgaveRepository.hentAlleOppgaveFilterSettTilknyttetEnhet(avdelingEnhet);
        var skalFraknyttes = oppgaveRepository.oppgaveFiltreringerForSaksbehandler(saksbehandler).stream()
            .filter(oppgaveFiltreringList::contains)
            .toList();
        for (var oppgaveFiltrering : skalFraknyttes) {
            oppgaveRepository.fraknyttSaksbehandlerOppgaveFiltrering(saksbehandler, oppgaveFiltrering);
        }
        oppgaveRepository.refresh(avdeling);
    }

    private Avdeling hentAvdeling(String avdelingEnhet) {
        return organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet).orElseThrow();
    }

    private Saksbehandler opprettSaksbehandler(String ident) {
        var ansattProfil = ansattTjeneste.hentBrukerProfil(ident)
            .orElseThrow(() -> AvdelingSaksbehandlerTjenesteFeil.finnerIkkeSaksbehandler(ident));
        var saksbehandler = new Saksbehandler(ident.trim().toUpperCase(), ansattProfil.navn(), ansattProfil.ansattAvdeling());
        organisasjonRepository.persistFlush(saksbehandler);
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(ident)
            .orElseThrow(() -> AvdelingSaksbehandlerTjenesteFeil.finnerIkkeSaksbehandler(ident));
    }

    public Saksbehandler oppdaterSaksbehandler(String ident) {
        var saksbehandler = organisasjonRepository.hentSaksbehandler(ident);
        var ansattProfil = ansattTjeneste.refreshBrukerProfil(ident);
        saksbehandler.setNavn(ansattProfil.map(BrukerProfil::navn).orElse(null));
        saksbehandler.setAnsattVedEnhet(ansattProfil.map(BrukerProfil::ansattAvdeling).orElse(null));
        organisasjonRepository.persistFlush(saksbehandler);
        return saksbehandler;
    }

    public List<SaksbehandlerGruppe> hentAvdelingensSaksbehandlereOgGrupper(String avdelingEnhet) {
        return organisasjonRepository.hentSaksbehandlerGrupper(avdelingEnhet);
    }

    public List<Saksbehandler> hentSaksbehandlereForGrupper(SaksbehandlerGruppe gruppe) {
        return organisasjonRepository.saksbehandlereForGruppe(gruppe);
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
