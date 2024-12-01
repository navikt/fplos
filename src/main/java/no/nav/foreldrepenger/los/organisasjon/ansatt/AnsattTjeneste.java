package no.nav.foreldrepenger.los.organisasjon.ansatt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    public Optional<BrukerProfil> hentBrukerProfilForLagretSaksbehandler(String saksbehandlerIdent) {
        try {
            return organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).map(this::hentBrukerProfil);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public BrukerProfil hentBrukerProfil(Saksbehandler saksbehandler) {
        var profil = saksbehandler.getSaksbehandlerUuidHvisFinnes().flatMap(this::hentBrukerProfil)
            .orElseGet(() -> hentBrukerProfil(saksbehandler.getSaksbehandlerIdent()));
        if (profil.uid() != null && !Objects.equals(profil.uid(), saksbehandler.getSaksbehandlerUuid())) {
            saksbehandler.setSaksbehandlerUuid(profil.uid());
            organisasjonRepository.persistFlush(saksbehandler);
        }
        return profil;
    }

    // TODO:  Her bør vi egentlig tenke om NOM er ikke riktigere å bruke - bør være raskere å slå opp navn og epost.
    // Jeg har sjekket med NOM (01.07.2024) og de støtter enn så lenge ikke Z-identer i dev. Men prod brukere er tilgjengelig.
    public BrukerProfil hentBrukerProfil(String ident) {
        var trimmed = ident.trim().toUpperCase();
        var brukerProfil = Optional.ofNullable(ANSATT_PROFIL.get(trimmed))
            .orElseGet(() -> mapTilDomene(brukerKlient.brukerProfil(trimmed)));
        // Alltid put for å extende levetid
        ANSATT_PROFIL.put(trimmed, brukerProfil);
        ANSATT_UID_PROFIL.put(brukerProfil.uid(), brukerProfil);
        return brukerProfil;
    }

    private Optional<BrukerProfil> hentBrukerProfil(UUID uid) {
        // For det spennende tilfelle at NAVident evt skulle bytte oid (slutter, reansatt?)
        try {
            var brukerProfil = Optional.ofNullable(ANSATT_UID_PROFIL.get(uid))
                .orElseGet(() -> mapTilDomene(brukerKlient.brukerProfil(uid)));
            // Alltid put for å extende levetid
            ANSATT_PROFIL.put(brukerProfil.ident().trim().toUpperCase(), brukerProfil);
            ANSATT_UID_PROFIL.put(uid, brukerProfil);
            return Optional.of(brukerProfil);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static BrukerProfil mapTilDomene(AzureBrukerKlient.BrukerProfilResponse klientResponse) {
        return new BrukerProfil(klientResponse.uid(), klientResponse.ident(), klientResponse.fornavnEtternavn(), avdeling(klientResponse.ansattVedEnhetId()));
    }

    private static String avdeling(String avdelingsNummer) {
        var avdelingsNavn = ENHETSNUMMER_AVDELINGSNAVN_MAP.get(avdelingsNummer);
        return avdelingsNavn != null ? avdelingsNavn : avdelingsNummer;
    }

}
