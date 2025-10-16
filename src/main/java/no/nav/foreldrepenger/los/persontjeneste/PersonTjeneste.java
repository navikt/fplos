package no.nav.foreldrepenger.los.persontjeneste;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.domene.typer.aktør.Fødselsnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.pdl.Folkeregisteridentifikator;
import no.nav.pdl.FolkeregisteridentifikatorResponseProjection;
import no.nav.pdl.HentIdenterQueryRequest;
import no.nav.pdl.HentPersonQueryRequest;
import no.nav.pdl.IdentGruppe;
import no.nav.pdl.IdentInformasjon;
import no.nav.pdl.IdentInformasjonResponseProjection;
import no.nav.pdl.Identliste;
import no.nav.pdl.IdentlisteResponseProjection;
import no.nav.pdl.NavnResponseProjection;
import no.nav.pdl.PersonResponseProjection;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.integrasjon.person.FalskIdentitet;
import no.nav.vedtak.felles.integrasjon.person.PdlException;
import no.nav.vedtak.felles.integrasjon.person.PersonMappers;
import no.nav.vedtak.felles.integrasjon.person.Persondata;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class PersonTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(PersonTjeneste.class);

    private static final int DEFAULT_CACHE_SIZE = 5000;
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS);

    private final LRUCache<AktørId, Person> cacheAktørIdTilPerson = new LRUCache<>(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_TIMEOUT);

    private final Persondata pdl;

    public PersonTjeneste() {
        this.pdl = new PdlKlient();
    }

    public Optional<Person> hentPerson(FagsakYtelseType ytelseType, AktørId aktørId, Saksnummer saksnummer) {
        Objects.requireNonNull(aktørId, "aktørId");
        var cachedPerson = cacheAktørIdTilPerson.get(aktørId);
        if (cachedPerson != null) {
            cacheAktørIdTilPerson.put(aktørId, cachedPerson);
            return Optional.of(cachedPerson);
        }
        try {
            var person = hentPdlPerson(ytelseType, aktørId, saksnummer);
            cacheAktørIdTilPerson.put(aktørId, person);
            return Optional.of(person);
        } catch (PdlException e) {
            if (e.getStatus() == HttpURLConnection.HTTP_NOT_FOUND) {
                return Optional.empty();
            }
            LOG.warn("PDL FPLOS hentPerson feil fra PDL ", e);
            if (e.getStatus() == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new IkkeTilgangPåPersonException(e);
            }
            throw e;
        }
    }

    private Person hentPdlPerson(FagsakYtelseType ytelseType, AktørId aktørId, Saksnummer saksnummer) {
        var query = new HentPersonQueryRequest();
        query.setIdent(aktørId.getId());
        var projection = new PersonResponseProjection().navn(new NavnResponseProjection().fornavn().mellomnavn().etternavn())
            .folkeregisteridentifikator(new FolkeregisteridentifikatorResponseProjection().identifikasjonsnummer().status().type());
        var ytelse = utledYtelse(ytelseType);
        var person = pdl.hentPerson(ytelse, query, projection);
        var fnr = fnr(person.getFolkeregisteridentifikator(), aktørId, saksnummer);
        if (harIdentifikator(person.getFolkeregisteridentifikator())) {
            return new Person(fnr, PersonMappers.mapNavn(person).orElse("Ukjent Navn"));
        } else  {
            // Falsk Identitet har navn i objekt. Utgått Identitet har Navn i Person
            var falskIdentitetNavn = hentNavnForFalskIdentitet(aktørId).or(() -> PersonMappers.mapNavn(person));
            return new Person(fnr, falskIdentitetNavn.orElse("Ukjent Navn"));
        }
    }

    private static boolean harIdentifikator(List<Folkeregisteridentifikator> folkeregisteridentifikator) {
        return folkeregisteridentifikator.stream()
            .anyMatch(i -> i.getStatus().equals("I_BRUK"));
    }

    private Fødselsnummer fnr(List<Folkeregisteridentifikator> folkeregisteridentifikator, AktørId aktørId, Saksnummer saksnummer) {
        var fraHentPerson = folkeregisteridentifikator.stream()
            .filter(i -> i.getStatus().equals("I_BRUK"))
            .map(Folkeregisteridentifikator::getIdentifikasjonsnummer)
            .map(Fødselsnummer::new)
            .findFirst();
        if (fraHentPerson.isEmpty()) {
            var fnr = hentFødselsnummerForAktørId(aktørId);
            if (fnr != null) {
                LOG.info("PDL mangler fnr i hentPerson for sak {} , fant FNR fra hentIdenter", saksnummer);
            } else {
                LOG.warn("PDL mangler fnr i hentPerson for sak {} , ingen FNR fra hentIdenter", saksnummer);
            }
            return fnr;
        }
        return fraHentPerson.orElseThrow(() -> new IllegalArgumentException("Fant ikke fødselsnummer"));
    }

    private Fødselsnummer hentFødselsnummerForAktørId(AktørId aktørId) {
        var request = new HentIdenterQueryRequest();
        request.setIdent(aktørId.getId());
        request.setGrupper(List.of(IdentGruppe.FOLKEREGISTERIDENT, IdentGruppe.NPID));
        request.setHistorikk(Boolean.FALSE);
        var projection = new IdentlisteResponseProjection().identer(new IdentInformasjonResponseProjection().ident());

        final Identliste identliste;

        try {
            identliste = pdl.hentIdenter(request, projection);
        } catch (VLException v) {
            if (Persondata.PDL_KLIENT_NOT_FOUND_KODE.equals(v.getKode())) {
                return null;
            }
            throw v;
        }
        return identliste.getIdenter().stream().findFirst().map(IdentInformasjon::getIdent).map(Fødselsnummer::new).orElse(null);
    }

    private Optional<String> hentNavnForFalskIdentitet(AktørId aktørId) {
        return FalskIdentitet.finnFalskIdentitet(aktørId.getId(), pdl).map(FalskIdentitet.Informasjon::navn);
    }

    private static Persondata.Ytelse utledYtelse(FagsakYtelseType ytelseType) {
        if (FagsakYtelseType.ENGANGSTØNAD.equals(ytelseType)) {
            return Persondata.Ytelse.ENGANGSSTØNAD;
        } else if (FagsakYtelseType.SVANGERSKAPSPENGER.equals(ytelseType)) {
            return Persondata.Ytelse.SVANGERSKAPSPENGER;
        } else {
            return Persondata.Ytelse.FORELDREPENGER;
        }
    }
}
