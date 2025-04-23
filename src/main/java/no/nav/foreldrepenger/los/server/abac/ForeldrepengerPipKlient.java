package no.nav.foreldrepenger.los.server.abac;

import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.vedtak.felles.integrasjon.fpsakpip.AbstractForeldrepengerPipKlient;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPSAK)
public class ForeldrepengerPipKlient extends AbstractForeldrepengerPipKlient {

    private static final int PIP_CACHE_SIZE = 3000;
    private static final int PIP_CACHE_TIMEOUT_MILLIS = 30000;

    private final LRUCache<Saksnummer, Set<String>> pipCache;

    public ForeldrepengerPipKlient() {
        super();
        this.pipCache = new LRUCache<>(PIP_CACHE_SIZE, PIP_CACHE_TIMEOUT_MILLIS);
    }

    public Set<String> hentPipdataForSak(Saksnummer saksnummer) {
        var cached = pipCache.get(saksnummer);
        if (cached != null) {
            return cached;
        }
        var ny = personerForSak(saksnummer.getVerdi());
        pipCache.put(saksnummer, new HashSet<>(ny));
        return pipCache.get(saksnummer);
    }

}
