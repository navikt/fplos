package no.nav.fplos.avdelingsleder;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.OrganisasjonRessursEnhetTjeneste;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBruker;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBrukeroppslag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AvdelingslederSaksbehandlerTjenesteImpl implements AvdelingslederSaksbehandlerTjeneste {

    private static final Logger log = LoggerFactory.getLogger(AvdelingslederSaksbehandlerTjeneste.class);
    private static final String LDAP = "LDAP";

    private OrganisasjonRepository organisasjonRepository;
    private OrganisasjonRessursEnhetTjeneste organisasjonRessursEnhetTjeneste;
    private OppgaveRepository oppgaveRepository;

    AvdelingslederSaksbehandlerTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public AvdelingslederSaksbehandlerTjenesteImpl(OppgaveRepository oppgaveRepository, OrganisasjonRepository organisasjonRepository, OrganisasjonRessursEnhetTjeneste organisasjonRessursEnhetTjeneste) {
        this.organisasjonRepository = organisasjonRepository;
        this.organisasjonRessursEnhetTjeneste = organisasjonRessursEnhetTjeneste;
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

    @Override
    public String hentSaksbehandlerNavn(String saksbehandlerIdent) {
        try {
            LdapBruker ldapBruker = new LdapBrukeroppslag().hentBrukerinformasjon(saksbehandlerIdent);
            return ldapBruker.getDisplayName();
        } catch (Exception e) {
            // FIXME: funksjonelt tåles det ikke å kaste exception her pt. Må skrives om.
            //throw AvdelingslederSaksbehandlerTjenesteFeil.FACTORY.feil(LDAP, e).toException();
            log.warn("Henting av saksbehandlers navn feilet, returnerer null.", e);
            return null;
        }
    }

    @Override
    public List<OrganisasjonsEnhet> hentSaksbehandlersAvdelinger(String saksbehandlerIdent) {
        return organisasjonRessursEnhetTjeneste.hentEnhetListe(saksbehandlerIdent);
    }

    private Saksbehandler saksbehandlerFra(String saksbehandlerIdent) {
        return new Saksbehandler(saksbehandlerIdent.toUpperCase());
    }
}
