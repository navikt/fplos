package no.nav.fplos.person;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.pdl.AdressebeskyttelseResponseProjection;
import no.nav.pdl.Folkeregisteridentifikator;
import no.nav.pdl.FolkeregisteridentifikatorResponseProjection;
import no.nav.pdl.HentPersonQueryRequest;
import no.nav.pdl.Navn;
import no.nav.pdl.NavnResponseProjection;
import no.nav.pdl.PersonResponseProjection;
import no.nav.vedtak.felles.integrasjon.pdl.Pdl;
import no.nav.vedtak.felles.integrasjon.pdl.PdlException;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@ApplicationScoped
public class PersonTjenesteImpl implements PersonTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(PersonTjenesteImpl.class);

    private Pdl pdl;

    public PersonTjenesteImpl() {
    }

    @Inject
    public PersonTjenesteImpl(@Jersey Pdl pdl) {
        this.pdl = pdl;
    }

    @Override
    public Optional<Person> hentPerson(AktørId aktørId) {
        Objects.requireNonNull(aktørId, "aktørId");
        try {
            return Optional.of(hentPdlPerson(aktørId)).map(p -> tilPerson(p, aktørId));
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
                .folkeregisteridentifikator(new FolkeregisteridentifikatorResponseProjection().identifikasjonsnummer().status().type());
        return pdl.hentPerson(query, projection);
    }

    private Person tilPerson(no.nav.pdl.Person person, AktørId aktørId) {
        return new Person.Builder()
                .medAktørId(aktørId)
                .medFnr(fnr(person.getFolkeregisteridentifikator()))
                .medNavn(navn(person.getNavn()))
                .build();
    }

    private static String navn(List<Navn> navn) {
        return navn.stream()
                .map(PersonTjenesteImpl::navn)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fant ikke navn"));
    }

    private static String navn(Navn navn) {
        if (navn.getForkortetNavn() != null) {
            return navn.getForkortetNavn();
        }
        return Stream.of(navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn())
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }

    private static Fødselsnummer fnr(List<Folkeregisteridentifikator> folkeregisteridentifikator) {
        return folkeregisteridentifikator.stream()
                .filter(i -> i.getStatus().equals("I_BRUK"))
                .map(Folkeregisteridentifikator::getIdentifikasjonsnummer)
                .map(Fødselsnummer::new)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fant ikke fødselsnummer"));
    }


}
