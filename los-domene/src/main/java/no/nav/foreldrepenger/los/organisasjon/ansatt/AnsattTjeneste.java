package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.vedtak.felles.integrasjon.ldap.LdapBrukeroppslag;

@ApplicationScoped
public class AnsattTjeneste {

    private EnhetstilgangTjeneste enhetstilgangTjeneste;
    private OrganisasjonRepository organisasjonRepository;
    private static List<String> aktuelleEnhetIder;

    AnsattTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjeneste(EnhetstilgangTjeneste enhetstilgangTjeneste,
                          OrganisasjonRepository organisasjonRepository) {
        this.enhetstilgangTjeneste = enhetstilgangTjeneste;
        this.organisasjonRepository = organisasjonRepository;
    }

    public String hentAnsattNavn(String ident) {
        var ldapBruker = new LdapBrukeroppslag().hentBrukerinformasjon(ident);
        return ldapBruker.displayName();
    }

    public List<String> hentAvdelingerNavnForAnsatt(String ident) {
        if (aktuelleEnhetIder == null) {
            aktuelleEnhetIder = organisasjonRepository.hentAvdelinger().stream()
                    .map(Avdeling::getAvdelingEnhet)
                    .collect(Collectors.toList());
        }
        return enhetstilgangTjeneste.hentEnhetstilganger(ident)
                .stream()
                .filter(oe -> aktuelleEnhetIder.contains(oe.id()))
                .map(OrganisasjonsEnhet::navn)
                .collect(Collectors.toList());
    }

}
