package no.nav.foreldrepenger.los.organisasjon.ansatt;

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

    private AnsattInfoKlient brukerKlient;
    private OrganisasjonRepository organisasjonRepository;


    AnsattTjeneste() {
        // for CDI proxy
    }

    @Inject
    public AnsattTjeneste(OrganisasjonRepository organisasjonRepository, AnsattInfoKlient brukerKlient) {
        this.brukerKlient = brukerKlient;
        this.organisasjonRepository = organisasjonRepository;
    }

    public Optional<BrukerProfil> hentBrukerProfilForLagretSaksbehandler(String saksbehandlerIdent) {
        try {
            return organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerIdent).flatMap(this::hentBrukerProfil);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public Optional<BrukerProfil> hentBrukerProfil(Saksbehandler saksbehandler) {
        var profil = saksbehandler.getSaksbehandlerUuidHvisFinnes().flatMap(this::hentBrukerProfil)
            .or(() -> hentBrukerProfil(saksbehandler.getSaksbehandlerIdent()))
            .orElse(null);
        if (profil != null && profil.uid() != null && !Objects.equals(profil.uid(), saksbehandler.getSaksbehandlerUuid())) {
            saksbehandler.setSaksbehandlerUuid(profil.uid());
            saksbehandler.setNavn(profil.navn());
            saksbehandler.setAnsattVedEnhet(profil.ansattAvdeling());
            organisasjonRepository.persistFlush(saksbehandler);
        }
        return Optional.ofNullable(profil);
    }

    // TODO:  Her bør vi egentlig tenke om NOM er ikke riktigere å bruke - bør være raskere å slå opp navn og epost.
    // Jeg har sjekket med NOM (01.07.2024) og de støtter enn så lenge ikke Z-identer i dev. Men prod brukere er tilgjengelig.
    public Optional<BrukerProfil> hentBrukerProfil(String ident) {
        var trimmed = ident.trim().toUpperCase();
        var brukerProfil = Optional.ofNullable(ANSATT_PROFIL.get(trimmed))
            .or(() -> brukerKlient.brukerProfil(trimmed));
        // Alltid put for å extende levetid
        brukerProfil.ifPresent(p -> ANSATT_PROFIL.put(trimmed, p));
        brukerProfil.ifPresent(p -> ANSATT_UID_PROFIL.put(p.uid(), p));
        return brukerProfil;
    }

    public Optional<BrukerProfil> refreshBrukerProfil(String ident) {
        var trimmed = ident.trim().toUpperCase();
        return brukerKlient.refreshAnsattInfoForIdent(trimmed);
    }


    private Optional<BrukerProfil> hentBrukerProfil(UUID uid) {
        // For det spennende tilfelle at NAVident evt skulle bytte oid (slutter, reansatt?)
        try {
            var brukerProfil = Optional.ofNullable(ANSATT_UID_PROFIL.get(uid))
                .or(() -> brukerKlient.brukerProfil(uid));
            // Alltid put for å extende levetid
            brukerProfil.ifPresent(p -> ANSATT_PROFIL.put(p.ident().trim().toUpperCase(), p));
            brukerProfil.ifPresent(p -> ANSATT_UID_PROFIL.put(uid, p));
            return brukerProfil;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
