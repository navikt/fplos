package no.nav.fplos.ansatt;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.OrganisasjonRessursEnhetTjeneste;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBruker;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBrukeroppslag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AnsattTjenesteImpl implements AnsattTjeneste {

    private OrganisasjonRessursEnhetTjeneste organisasjonRessursEnhetTjeneste;

    AnsattTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjenesteImpl(OrganisasjonRessursEnhetTjeneste organisasjonRessursEnhetTjeneste) {
        this.organisasjonRessursEnhetTjeneste = organisasjonRessursEnhetTjeneste;
    }

    @Override
    public String hentAnsattNavn(String ident) {
        LdapBruker ldapBruker = new LdapBrukeroppslag().hentBrukerinformasjon(ident);
        return ldapBruker.getDisplayName();
    }

    @Override
    public List<String> hentAvdelingerNavnForAnsatt(String ident) {
        return organisasjonRessursEnhetTjeneste.hentEnhetListe(ident)
                .stream()
                .map(OrganisasjonsEnhet::getEnhetNavn)
                .collect(Collectors.toList());
    }
}
