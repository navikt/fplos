package no.nav.fplos.person;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class PersonTjeneste {

    private static final int DEFAULT_CACHE_SIZE = 1000;
    //Satt til 8 timer for å matche cache-lengde brukt i ABAC-løsningen (PDP).
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS);

    private TpsAdapter tpsAdapter;
    private LRUCache<Fødselsnummer, Optional<Person>> cacheFødselsnummerTilPerson;
    private LRUCache<AktørId, Optional<Person>> cacheAktørTilPerson;

    PersonTjeneste() {
        // for CDI proxy
    }

    @Inject
    public PersonTjeneste(TpsAdapter tpsAdapter) {
        this(tpsAdapter, DEFAULT_CACHE_SIZE, DEFAULT_CACHE_TIMEOUT);
    }

    private PersonTjeneste(TpsAdapter tpsAdapter, int cacheSize, long cacheTimeoutMillis) {
        this.tpsAdapter = tpsAdapter;
        this.cacheFødselsnummerTilPerson = new LRUCache<>(cacheSize, cacheTimeoutMillis);
        this.cacheAktørTilPerson = new LRUCache<>(cacheSize, cacheTimeoutMillis);
    }

    public Optional<Person> hentPerson(Fødselsnummer fnr) {
        Optional<Person> cachet = cacheFødselsnummerTilPerson.get(fnr);
        if (cachet != null) { // NOSONAR null betyr finnes ikke i cache, empty betyr finnes ikke i tps
            return cachet;
        }
        var person = tpsAdapter.hentPerson(fnr);
        cacheFødselsnummerTilPerson.put(fnr, person);
        return tpsAdapter.hentPerson(fnr);
    }

    public Optional<Person> hentPerson(AktørId aktørId) {
        Optional<Person> cachet = cacheAktørTilPerson.get(aktørId);
        if (cachet != null) { // NOSONAR trenger null-sjekk selv om bruker optional. Null betyr "finnes ikke i cache". Optional.empty betyr "finnes ikke i TPS"
            return cachet;
        }
        var person = tpsAdapter.hentPerson(aktørId);
        cacheAktørTilPerson.put(aktørId, person);
        return person;
    }
}
