package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.vedtak.util.LRUCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AnsattTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AnsattTjeneste.class);

    private static final LRUCache<String, BrukerProfil> ANSATT_PROFIL = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(24 * 7, TimeUnit.HOURS));
    private static final LRUCache<String, List<String>> ANSATT_ENHETER = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(25, TimeUnit.HOURS));

    private EnhetstilgangTjeneste enhetstilgangTjeneste;
    private OrganisasjonRepository organisasjonRepository;
    private List<String> aktuelleEnhetIder;

    AnsattTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjeneste(EnhetstilgangTjeneste enhetstilgangTjeneste,
                          OrganisasjonRepository organisasjonRepository) {
        this.enhetstilgangTjeneste = enhetstilgangTjeneste;
        this.organisasjonRepository = organisasjonRepository;
    }

    public BrukerProfil hentBrukerProfil(String ident) {
        if (ANSATT_PROFIL.get(ident) == null) {
            var brukerProfil = new LdapBrukeroppslag().hentBrukerProfil(ident);
            sammenlignMedAzureGraphFailSoft(ident, brukerProfil);
            ANSATT_PROFIL.put(ident, brukerProfil);
        }
        return ANSATT_PROFIL.get(ident);
    }

    private static void sammenlignMedAzureGraphFailSoft(String ident, BrukerProfil ldapBrukerInfo) {
        LOG.info("PROFIL Azure. Henter fra azure.");
        try {
            var azureBrukerProfil = new AzureBrukerKlient().brukerProfil(ident);
            if (!ldapBrukerInfo.equals(azureBrukerProfil)) {
                LOG.info("PROFIL Azure. Profiler fra ldap og azure er ikke like. Azure: {} != LDAP: {}", azureBrukerProfil, ldapBrukerInfo);
            } else {
                LOG.info("PROFIL Azure. Azure == LDAP :)");
            }
        } catch (Exception ex) {
            LOG.info("PROFIL Azure. Klienten feilet med exception: {}", ex.getMessage());
        }
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
