package no.nav.foreldrepenger.los.klient.person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.domene.typer.aktør.Fødselsnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.pdl.Adressebeskyttelse;
import no.nav.pdl.AdressebeskyttelseGradering;
import no.nav.pdl.AdressebeskyttelseResponseProjection;
import no.nav.pdl.Doedsfall;
import no.nav.pdl.DoedsfallResponseProjection;
import no.nav.pdl.Foedsel;
import no.nav.pdl.FoedselResponseProjection;
import no.nav.pdl.Folkeregisteridentifikator;
import no.nav.pdl.FolkeregisteridentifikatorResponseProjection;
import no.nav.pdl.HentPersonQueryRequest;
import no.nav.pdl.Kjoenn;
import no.nav.pdl.KjoennResponseProjection;
import no.nav.pdl.KjoennType;
import no.nav.pdl.Navn;
import no.nav.pdl.NavnResponseProjection;
import no.nav.pdl.PersonResponseProjection;
import no.nav.vedtak.felles.integrasjon.pdl.Pdl;
import no.nav.vedtak.felles.integrasjon.pdl.PdlException;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class PersonTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(PersonTjeneste.class);

    private static final int DEFAULT_CACHE_SIZE = 2000;
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS);

    private final LRUCache<AktørId, Person> cacheAktørIdTilPerson = new LRUCache<>(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_TIMEOUT);

    private Pdl pdl;

    @Inject
    public PersonTjeneste(@Jersey Pdl pdl) {
        this.pdl = pdl;
    }

    public PersonTjeneste() {
    }

    public Optional<Person> hentPerson(AktørId aktørId) {
        Objects.requireNonNull(aktørId, "aktørId");
        if (cacheAktørIdTilPerson.get(aktørId) != null) return Optional.of(cacheAktørIdTilPerson.get(aktørId));
        try {
            var person = tilPerson(hentPdlPerson(aktørId));
            cacheAktørIdTilPerson.put(aktørId, person);
            return Optional.of(person);
        } catch (PdlException e) {
            if (e.getStatus() == HttpStatus.SC_NOT_FOUND) {
                return Optional.empty();
            }
            LOG.warn("PDL FPLOS hentPerson feil fra PDL ", e);
            if (e.getStatus() == HttpStatus.SC_FORBIDDEN) {
                throw new IkkeTilgangPåPersonException(e);
            }
            throw e;
        }
    }

    private no.nav.pdl.Person hentPdlPerson(AktørId aktørId) {
        var query = new HentPersonQueryRequest();
        query.setIdent(aktørId.getId());
        var projection = new PersonResponseProjection()
                .navn(new NavnResponseProjection().forkortetNavn().fornavn().mellomnavn().etternavn())
                .adressebeskyttelse(new AdressebeskyttelseResponseProjection().gradering())
                .folkeregisteridentifikator(new FolkeregisteridentifikatorResponseProjection().identifikasjonsnummer().status().type())
                .foedsel(new FoedselResponseProjection().foedselsdato())
                .doedsfall(new DoedsfallResponseProjection().doedsdato())
                .kjoenn(new KjoennResponseProjection().kjoenn());
        return pdl.hentPerson(query, projection);
    }

    private Person tilPerson(no.nav.pdl.Person person) {
        var fødselsdato = person.getFoedsel().stream()
                .map(Foedsel::getFoedselsdato)
                .filter(Objects::nonNull)
                .findFirst().map(d -> LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null);
        var dødsdato = person.getDoedsfall().stream()
                .map(Doedsfall::getDoedsdato)
                .filter(Objects::nonNull)
                .findFirst().map(d -> LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null);
        return new Person.Builder()
                .medFnr(fnr(person.getFolkeregisteridentifikator()))
                .medNavn(navn(person.getNavn()))
                .medFødselsdato(fødselsdato)
                .medDødsdato(dødsdato)
                .medKjønn(mapKjønn(person))
                .medDiskresjonskode(getDiskresjonskode(person))
                .build();
    }

    private static String navn(List<Navn> navn) {
        return navn.stream()
                .map(PersonTjeneste::navn)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fant ikke navn"));
    }

    private static String navn(Navn navn) {
        if (navn.getForkortetNavn() != null)
            return navn.getForkortetNavn();
        return navn.getEtternavn() + " " + navn.getFornavn() + (navn.getMellomnavn() == null ? "" : " " + navn.getMellomnavn());
    }

    private static Fødselsnummer fnr(List<Folkeregisteridentifikator> folkeregisteridentifikator) {
        return folkeregisteridentifikator.stream()
                .filter(i -> i.getStatus().equals("I_BRUK"))
                .map(Folkeregisteridentifikator::getIdentifikasjonsnummer)
                .map(Fødselsnummer::new)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fant ikke fødselsnummer"));
    }

    private static NavBrukerKjønn mapKjønn(no.nav.pdl.Person person) {
        var kode = person.getKjoenn().stream()
                .map(Kjoenn::getKjoenn)
                .filter(Objects::nonNull)
                .findFirst().orElse(KjoennType.UKJENT);
        return KjoennType.KVINNE.equals(kode) ? NavBrukerKjønn.K : NavBrukerKjønn.M;
    }

    private static String getDiskresjonskode(no.nav.pdl.Person person) {
        var kode = person.getAdressebeskyttelse().stream()
                .map(Adressebeskyttelse::getGradering)
                .filter(g -> !AdressebeskyttelseGradering.UGRADERT.equals(g))
                .findFirst().orElse(null);

        // TODO: lag kodeverk som passer med losfront sin diskresjonskodeType.ts - trenger bare disse 2
        if (AdressebeskyttelseGradering.STRENGT_FORTROLIG.equals(kode) || AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND.equals(kode))
            return "SPSF";
        return AdressebeskyttelseGradering.FORTROLIG.equals(kode) ? "SPFO" : null;
    }
}
