package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;

@ApplicationScoped
public class AnsattTjeneste {

    private EnhetstilgangTjeneste enhetstilgangTjeneste;
    private OrganisasjonRepository organisasjonRepository;
    private List<String> aktuelleEnhetIder;

    AnsattTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjeneste(EnhetstilgangTjeneste enhetstilgangTjeneste, OrganisasjonRepository organisasjonRepository) {
        this.enhetstilgangTjeneste = enhetstilgangTjeneste;
        this.organisasjonRepository = organisasjonRepository;
    }

    public String hentAnsattNavn(String ident) {
        return new LdapBrukeroppslag().hentBrukersNavn(ident);
    }

    public List<String> hentAvdelingerNavnForAnsatt(String ident) {
        if (aktuelleEnhetIder == null) {
            aktuelleEnhetIder = organisasjonRepository.hentAvdelinger().stream().map(Avdeling::getAvdelingEnhet).toList();
        }
        return enhetstilgangTjeneste.hentEnhetstilganger(ident)
            .stream()
            .filter(oe -> aktuelleEnhetIder.contains(oe.enhetId()))
            .map(OrganisasjonsEnhet::navn)
            .toList();
    }

}
