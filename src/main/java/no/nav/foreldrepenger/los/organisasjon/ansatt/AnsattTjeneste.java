package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class AnsattTjeneste {

    private static final LRUCache<String, BrukerProfil> ANSATT_PROFIL = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(24 * 7, TimeUnit.HOURS));
    private static final Map<String, String> ENHETSNUMMER_AVDELINGSNAVN_MAP = new HashMap<>();


    AnsattTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjeneste(OrganisasjonRepository organisasjonRepository) {
        organisasjonRepository.hentAktiveAvdelinger().forEach(a -> ENHETSNUMMER_AVDELINGSNAVN_MAP.put(a.getAvdelingEnhet(), a.getNavn()));
    }

    public BrukerProfil hentBrukerProfil(String ident) {
        if (ANSATT_PROFIL.get(ident) == null) {
            //TODO:  Her bør vi egentlig tenke om NOM er ikke riktigere å bruke - bør være raskere å slå opp navn og epost.
            // Jeg har sjekket med NOM (01.07.2024) og de støtter en så lenge ikke Z-identer i dev. Men prod brukere er tilgjengelig.
            var brukerProfil = mapTilDomene(new AzureBrukerKlient().brukerProfil(ident));
            ANSATT_PROFIL.put(ident, brukerProfil);
        }
        return ANSATT_PROFIL.get(ident);
    }

    private static BrukerProfil mapTilDomene(AzureBrukerKlient.BrukerProfilResponse klientResponse) {
        return new BrukerProfil(klientResponse.ident(), klientResponse.fornavnEtternavn(), klientResponse.fornavnEtternavn(), klientResponse.epost(),
            avdeling(klientResponse.ansattVedEnhetId()));
    }

    private static String avdeling(String avdelingsNummer) {
        var avdelingsNavn = ENHETSNUMMER_AVDELINGSNAVN_MAP.get(avdelingsNummer);
        return avdelingsNavn != null ? avdelingsNavn : avdelingsNummer;
    }

}
