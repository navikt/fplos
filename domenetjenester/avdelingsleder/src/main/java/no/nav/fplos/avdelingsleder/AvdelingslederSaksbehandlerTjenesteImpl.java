package no.nav.fplos.avdelingsleder;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.OrganisasjonRessursEnhetTjeneste;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBruker;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBrukeroppslag;

@ApplicationScoped
public class AvdelingslederSaksbehandlerTjenesteImpl implements AvdelingslederSaksbehandlerTjeneste {

    private static final String LDAP = "LDAP";

    private OrganisasjonRepository organisasjonRepository;
    private OrganisasjonRessursEnhetTjeneste organisasjonRessursEnhetTjeneste;
    private OppgaveRepository oppgaveRepository;

    AvdelingslederSaksbehandlerTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederSaksbehandlerTjenesteImpl(OppgaveRepositoryProvider oppgaveRepositoryProvider, OrganisasjonRessursEnhetTjeneste organisasjonRessursEnhetTjeneste) {
        organisasjonRepository = oppgaveRepositoryProvider.getOrganisasjonRepository();
        this.organisasjonRessursEnhetTjeneste = organisasjonRessursEnhetTjeneste;
        oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
    }

    @Override
    public List<Saksbehandler> hentAvdelingensSaksbehandlere(String avdelingEnhet) {
        return organisasjonRepository.hentAvdelingensSaksbehandlere(avdelingEnhet);
    }

    @Override
    public void leggTilSaksbehandler(String saksbehandlerIdent, String avdelingEnhet) {
        Saksbehandler saksbehandler;
        Optional<Saksbehandler> optionalSaksbehandler = organisasjonRepository.hentMuligSaksbehandler(saksbehandlerIdent);
        if (!optionalSaksbehandler.isPresent()){
            organisasjonRepository.lagre(new Saksbehandler(saksbehandlerIdent.toUpperCase()));
            saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        }else{
            saksbehandler = optionalSaksbehandler.get();
        }
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet);
        saksbehandler.leggTilAvdeling(avdeling);

        organisasjonRepository.lagre(saksbehandler);
        organisasjonRepository.refresh(avdeling);
    }

    @Override
    public void slettSaksbehandler(String saksbehandlerIdent, String avdelingEnhet) {
        Saksbehandler saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerIdent);
        saksbehandler.fjernAvdeling(organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet));
        organisasjonRepository.lagre(saksbehandler);

        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingEnhet);
        List<OppgaveFiltrering> oppgaveFiltreringList = avdeling.getOppgaveFiltrering();
        for(OppgaveFiltrering oppgaveFiltrering: oppgaveFiltreringList){
            oppgaveFiltrering.fjernSaksbehandler(saksbehandler);
            oppgaveRepository.lagre(oppgaveFiltrering);
        }
        oppgaveRepository.refresh(avdeling);
    }

    @Override
    public String hentSaksbehandlerNavn(String saksbehandlerIdent) {
        try {
            LdapBruker ldapBruker = new LdapBrukeroppslag().hentBrukerinformasjon(saksbehandlerIdent);
            return ldapBruker.getDisplayName();
        } catch (Exception e) {
            AvdelingslederSaksbehandlerTjenesteFeil.FACTORY.feil(LDAP, e);
            return null;
        }
    }

    @Override
    public List<OrganisasjonsEnhet> hentSaksbehandlersAvdelinger(String saksbehandlerIdent) {
        return organisasjonRessursEnhetTjeneste.hentEnhetListe(saksbehandlerIdent);
    }
}
