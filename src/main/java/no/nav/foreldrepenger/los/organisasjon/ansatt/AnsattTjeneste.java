package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class AnsattTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AnsattTjeneste.class);

    private static final LRUCache<String, BrukerProfil> ANSATT_PROFIL = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(24 * 7, TimeUnit.HOURS));
    private static final LRUCache<String, List<String>> ANSATT_ENHETER = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(25, TimeUnit.HOURS));
    private static final Map<String, String> ENHETSNUMMER_AVDELINGSNAVN_MAP = new HashMap<>();

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
        organisasjonRepository.hentAktiveAvdelinger().forEach(a -> ENHETSNUMMER_AVDELINGSNAVN_MAP.put(a.getAvdelingEnhet(), a.getNavn()));
    }

    public BrukerProfil hentBrukerProfil(String ident) {
        if (ANSATT_PROFIL.get(ident) == null) {
            //TODO:  Her bør vi egentlig tenke om NOM er ikke riktigere å bruke - bør være raskere å slå opp navn og epost.
            // Jeg har sjekket med NOM (01.07.2024) og de støtter en så lenge ikke Z-identer i dev. Men prod brukere er tilgjengelig.
            var før = System.nanoTime();
            var ldapRespons = new LdapBrukeroppslag().hentBrukerProfil(ident);
            LOG.info("LDAP bruker profil oppslag: {}ms. ", Duration.ofNanos(System.nanoTime() - før).toMillis());
            var ansattEnhet = avdeling(ldapRespons.ansattEnhet());
            if (ansattEnhet == null) {
                LOG.info("PROFIL LDAP: brukers enhet {} ikke blant saksbehandlingsenhetene", ldapRespons.ansattEnhet());
            }
            var brukerProfil = new BrukerProfil(ldapRespons.ident(), ldapRespons.navn(), ldapRespons.fornavnEtternavn(), ldapRespons.epostAdresse(),
                ansattEnhet);
            sammenlignMedAzureGraphFailSoft(ident, brukerProfil);
            ANSATT_PROFIL.put(ident, brukerProfil);
        }
        return ANSATT_PROFIL.get(ident);
    }

    private static void sammenlignMedAzureGraphFailSoft(String ident, BrukerProfil ldapBrukerInfo) {
        LOG.info("PROFIL Azure. Henter fra azure.");
        try {
            var før = System.nanoTime();
            var azureBrukerProfil = mapTilDomene(new AzureBrukerKlient().brukerProfil(ident));
            if (!ldapBrukerInfo.equals(azureBrukerProfil)) {
                LOG.info("PROFIL Azure. Profiler fra ldap og azure er ikke like. Azure: {} != LDAP: {}", azureBrukerProfil, ldapBrukerInfo);
            } else {
                LOG.info("PROFIL Azure. Azure == LDAP :)");
            }
            LOG.info("Azure bruker profil oppslag: {}ms. ", Duration.ofNanos(System.nanoTime() - før).toMillis());
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

    private static BrukerProfil mapTilDomene(AzureBrukerKlient.BrukerProfilResponse klientResponse) {
        return new BrukerProfil(klientResponse.ident(), klientResponse.navn(), klientResponse.fornavnEtternavn(), klientResponse.epost(),
            avdeling(klientResponse.ansattVedEnhetId()));
    }

    private static String avdeling(String avdelingsNummer) {
        var avdelingsNavn = ENHETSNUMMER_AVDELINGSNAVN_MAP.get(avdelingsNummer);
        return avdelingsNavn != null ? avdelingsNavn : avdelingsNummer;
    }

}
