package no.nav.fplos.ansatt;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBruker;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBrukeroppslag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AnsattTjenesteImpl implements AnsattTjeneste {

    private EnhetstilgangTjeneste enhetstilgangTjeneste;

    AnsattTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjenesteImpl(EnhetstilgangTjeneste enhetstilgangTjeneste) {
        this.enhetstilgangTjeneste = enhetstilgangTjeneste;
    }

    @Override
    public String hentAnsattNavn(String ident) {
        LdapBruker ldapBruker = new LdapBrukeroppslag().hentBrukerinformasjon(ident);
        return ldapBruker.getDisplayName();
    }

    @Override
    public List<String> hentAvdelingerNavnForAnsatt(String ident) {
        return enhetstilgangTjeneste.hentEnhetstilganger(ident)
                .stream()
                .map(OrganisasjonsEnhet::getEnhetNavn)
                .collect(Collectors.toList());
    }
}
