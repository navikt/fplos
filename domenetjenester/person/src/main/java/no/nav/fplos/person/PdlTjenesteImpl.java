package no.nav.fplos.person;

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
import no.nav.vedtak.felles.integrasjon.pdl.PdlKlient;
import no.nav.vedtak.felles.integrasjon.pdl.Tema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class PdlTjenesteImpl implements PdlTjeneste {

    private PdlKlient pdlKlient;

    public PdlTjenesteImpl() {
    }

    @Inject
    public PdlTjenesteImpl(PdlKlient pdlKlient) {
        this.pdlKlient = pdlKlient;
    }

    @Override
    public Optional<Person> hentPerson(AktørId aktørId) {
            var query = new HentPersonQueryRequest();
            query.setIdent(aktørId.getId());
            var projection = new PersonResponseProjection()
                    .navn(new NavnResponseProjection().forkortetNavn().fornavn().mellomnavn().etternavn())
                    .adressebeskyttelse(new AdressebeskyttelseResponseProjection().gradering())
                    .folkeregisteridentifikator(new FolkeregisteridentifikatorResponseProjection().identifikasjonsnummer().status().type());
            var pdlPerson = pdlKlient.hentPerson(query, projection, Tema.FOR);
            return Optional.ofNullable(pdlPerson).map(p -> tilPerson(p, aktørId));
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
                .map(PdlTjenesteImpl::navn)
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
