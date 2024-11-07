package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class AnsattTjeneste {

    private static final LRUCache<String, BrukerProfil> ANSATT_PROFIL = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(24 * 7, TimeUnit.HOURS));
    private static final LRUCache<UUID, BrukerProfil> ANSATT_UID_PROFIL = new LRUCache<>(1000, TimeUnit.MILLISECONDS.convert(24 * 7, TimeUnit.HOURS));
    private static final Map<String, String> ENHETSNUMMER_AVDELINGSNAVN_MAP = new HashMap<>();

    private AzureBrukerKlient brukerKlient;
    private OrganisasjonRepository organisasjonRepository;


    AnsattTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjeneste(OrganisasjonRepository organisasjonRepository, AzureBrukerKlient brukerKlient) {
        this.brukerKlient = brukerKlient;
        this.organisasjonRepository = organisasjonRepository;
        organisasjonRepository.hentAktiveAvdelinger().forEach(a -> ENHETSNUMMER_AVDELINGSNAVN_MAP.put(a.getAvdelingEnhet(), a.getNavn()));
    }

    public Optional<BrukerProfil> hentBrukerProfilHvisIdentFinnes(String saksbehandlerIdent) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).map(this::hentBrukerProfil);
    }

    public BrukerProfil hentBrukerProfil(Saksbehandler saksbehandler) {
        return saksbehandler.getSaksbehandlerUuidHvisFinnes().map(this::hentBrukerProfil)
            .orElseGet(() -> hentBrukerProfil(saksbehandler.getSaksbehandlerIdent()));
    }

    public BrukerProfil hentBrukerProfil(String ident) {
        if (ANSATT_PROFIL.get(ident.trim().toUpperCase()) == null) {
            //TODO:  Her bør vi egentlig tenke om NOM er ikke riktigere å bruke - bør være raskere å slå opp navn og epost.
            // Jeg har sjekket med NOM (01.07.2024) og de støtter en så lenge ikke Z-identer i dev. Men prod brukere er tilgjengelig.
            var brukerProfil = mapTilDomene(brukerKlient.brukerProfil(ident.trim().toUpperCase()));
            ANSATT_PROFIL.put(ident.trim().toUpperCase(), brukerProfil);
            ANSATT_UID_PROFIL.put(brukerProfil.uid(), brukerProfil);
        }
        return ANSATT_PROFIL.get(ident.trim().toUpperCase());
    }

    public BrukerProfil hentBrukerProfil(UUID uid) {
        if (ANSATT_UID_PROFIL.get(uid) == null) {
            var brukerProfil = mapTilDomene(brukerKlient.brukerProfil(uid));
            ANSATT_PROFIL.put(brukerProfil.ident().trim().toUpperCase(), brukerProfil);
            ANSATT_UID_PROFIL.put(uid, brukerProfil);
        }
        return ANSATT_UID_PROFIL.get(uid);
    }

    private static BrukerProfil mapTilDomene(AzureBrukerKlient.BrukerProfilResponse klientResponse) {
        return new BrukerProfil(klientResponse.uid(), klientResponse.ident(), klientResponse.fornavnEtternavn(), avdeling(klientResponse.ansattVedEnhetId()));
    }

    private static String avdeling(String avdelingsNummer) {
        var avdelingsNavn = ENHETSNUMMER_AVDELINGSNAVN_MAP.get(avdelingsNummer);
        return avdelingsNavn != null ? avdelingsNavn : avdelingsNummer;
    }

}
