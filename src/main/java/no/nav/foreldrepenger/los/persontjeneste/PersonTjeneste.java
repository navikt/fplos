package no.nav.foreldrepenger.los.persontjeneste;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.domene.typer.aktør.Fødselsnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.pdl.Adressebeskyttelse;
import no.nav.pdl.AdressebeskyttelseGradering;
import no.nav.pdl.AdressebeskyttelseResponseProjection;
import no.nav.pdl.Folkeregisteridentifikator;
import no.nav.pdl.FolkeregisteridentifikatorResponseProjection;
import no.nav.pdl.HentIdenterQueryRequest;
import no.nav.pdl.HentPersonBolkQueryRequest;
import no.nav.pdl.HentPersonBolkResult;
import no.nav.pdl.HentPersonBolkResultResponseProjection;
import no.nav.pdl.HentPersonQueryRequest;
import no.nav.pdl.IdentGruppe;
import no.nav.pdl.IdentInformasjon;
import no.nav.pdl.IdentInformasjonResponseProjection;
import no.nav.pdl.Identliste;
import no.nav.pdl.IdentlisteResponseProjection;
import no.nav.pdl.Navn;
import no.nav.pdl.NavnResponseProjection;
import no.nav.pdl.PersonResponseProjection;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.integrasjon.person.PdlException;
import no.nav.vedtak.felles.integrasjon.person.Persondata;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class PersonTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(PersonTjeneste.class);

    private static final int DEFAULT_CACHE_SIZE = 2000;
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS);

    private final LRUCache<AktørId, Person> cacheAktørIdTilPerson = new LRUCache<>(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_TIMEOUT);

    private final Persondata pdl;

    public PersonTjeneste() {
        this.pdl = new PdlKlient();
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

    public Optional<Person> hentPerson(FagsakYtelseType ytelseType, AktørId aktørId, String saksnummer) {
        Objects.requireNonNull(aktørId, "aktørId");
        if (cacheAktørIdTilPerson.get(aktørId) != null) {
            return Optional.of(cacheAktørIdTilPerson.get(aktørId));
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

    private Person hentPdlPerson(FagsakYtelseType ytelseType, AktørId aktørId, String saksnummer) {
        var query = new HentPersonQueryRequest();
        query.setIdent(aktørId.getId());
        var projection = new PersonResponseProjection().navn(new NavnResponseProjection().fornavn().mellomnavn().etternavn())
            .adressebeskyttelse(new AdressebeskyttelseResponseProjection().gradering())
            .folkeregisteridentifikator(new FolkeregisteridentifikatorResponseProjection().identifikasjonsnummer().status().type());
        var ytelse = utledYtelse(ytelseType);
        var person = pdl.hentPerson(ytelse, query, projection);
        var fnr = fnr(person.getFolkeregisteridentifikator(), aktørId, saksnummer);
        return new Person.Builder().medFnr(fnr)
            .medNavn(navn(person.getNavn()))
            .build();
    }

    private static String navn(List<Navn> navn) {
        return navn.stream().map(PersonTjeneste::navn).findFirst().orElseThrow(() -> new IllegalArgumentException("Fant ikke navn"));
    }

    private static String navn(Navn navn) {
        return navn.getFornavn() + leftPad(navn.getMellomnavn()) + leftPad(navn.getEtternavn());
    }

    private static String leftPad(String navn) {
        return Optional.ofNullable(navn).map(n -> " " + navn).orElse("");
    }

    private Fødselsnummer fnr(List<Folkeregisteridentifikator> folkeregisteridentifikator, AktørId aktørId, String saksnummer) {
        var fraHentPerson = folkeregisteridentifikator.stream()
            .filter(i -> i.getStatus().equals("I_BRUK"))
            .map(Folkeregisteridentifikator::getIdentifikasjonsnummer)
            .map(Fødselsnummer::new)
            .findFirst();
        if (fraHentPerson.isEmpty()) {
            var fnr = hentFødselsnummerForAktørId(aktørId);
            if (fnr != null) {
                LOG.warn("PDL mangler fnr i hentPerson for sak {} , fant FNR fra hentIdenter", saksnummer);
            } else {
                LOG.warn("PDL mangler fnr i hentPerson for sak {} , ingen FNR fra hentIdenter", saksnummer);
            }
            return fnr;
        }
        return fraHentPerson.orElseThrow(() -> new IllegalArgumentException("Fant ikke fødselsnummer"));
    }

    public boolean harNoenKode7MenIngenHarKode6(FagsakYtelseType ytelseType, List<String> aktørIds) {
        var query = new HentPersonBolkQueryRequest();
        query.setIdenter(aktørIds);
        var projection = new HentPersonBolkResultResponseProjection()
            .person(new PersonResponseProjection()
                .adressebeskyttelse(new AdressebeskyttelseResponseProjection().gradering()));
        var ytelse = utledYtelse(ytelseType);
        var personer = pdl.hentPersonBolk(ytelse, query, projection);
        var adresseBeskyttelser = personer.stream()
            .map(HentPersonBolkResult::getPerson)
            .map(no.nav.pdl.Person::getAdressebeskyttelse)
            .flatMap(Collection::stream)
            .map(Adressebeskyttelse::getGradering)
            .collect(Collectors.toSet());
        var harNoenKode6 = adresseBeskyttelser.contains(AdressebeskyttelseGradering.STRENGT_FORTROLIG)
            || adresseBeskyttelser.contains(AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND);
        var harNoenKode7 = adresseBeskyttelser.contains(AdressebeskyttelseGradering.FORTROLIG);
        return harNoenKode7 && !harNoenKode6;
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
