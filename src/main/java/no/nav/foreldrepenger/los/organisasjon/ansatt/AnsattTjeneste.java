package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class AnsattTjeneste {

    private static final LRUCache<String, String> ANSATT_PROFIL = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(24 * 7, TimeUnit.HOURS));
    private static final LRUCache<String, List<String>> ANSATT_ENHETER = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(25, TimeUnit.HOURS));

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

    public BrukerProfil hentBrukerProfil(String ident) {
        if (ANSATT_PROFIL.get(ident) == null) {
            // TODO: Erstatt med MS Graph API
            var brukerProfil = new LdapBrukeroppslag().hentBrukerProfil(ident);
            ANSATT_PROFIL.put(ident, brukerProfil);
        }
        return ANSATT_PROFIL.get(ident);
    }

    public List<String> hentAvdelingerNavnForAnsatt(String ident) {
        if (aktuelleEnhetIder == null) {
            aktuelleEnhetIder = organisasjonRepository.hentAktiveAvdelinger().stream().map(Avdeling::getAvdelingEnhet).toList();
        }
        if (ANSATT_ENHETER.get(ident) == null) {
            // TODO: Fjerne axsys. Alternativt vise kun epost
            var enheter = enhetstilgangTjeneste.hentEnhetstilganger(ident)
                .stream()
                .filter(oe -> aktuelleEnhetIder.contains(oe.enhetId()))
                .map(OrganisasjonsEnhet::navn)
                .toList();
            ANSATT_ENHETER.put(ident, enheter);
        }
        return ANSATT_ENHETER.get(ident);
    }

}
