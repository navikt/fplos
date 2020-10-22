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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class PdlTjenesteImpl implements PdlTjeneste {

    private static final Logger log = LoggerFactory.getLogger(PdlTjenesteImpl.class);
    private PdlKlient pdlKlient;

    public PdlTjenesteImpl() {
    }

    @Inject
    public PdlTjenesteImpl(PdlKlient pdlKlient) {
        this.pdlKlient = pdlKlient;
    }

    @Override
    public void hentPerson(AktørId aktørId, Person personFraTps) {
        try {
            var query = new HentPersonQueryRequest();
            query.setIdent(aktørId.getId());
            var projection = new PersonResponseProjection()
                    .navn(new NavnResponseProjection().forkortetNavn().fornavn().mellomnavn().etternavn())
                    .adressebeskyttelse(new AdressebeskyttelseResponseProjection().gradering())
                    .folkeregisteridentifikator(new FolkeregisteridentifikatorResponseProjection().identifikasjonsnummer().status().type());
            var person = pdlKlient.hentPerson(query, projection, Tema.FOR);
            var fraPDL = new Person.Builder()
                    .medAktørId(aktørId)
                    .medFnr(fnr(person.getFolkeregisteridentifikator()))
                    .medNavn(navn(person.getNavn()))
                    .build();
            if (Objects.equals(fraPDL, personFraTps)) {
                log.info("Pdl/tps person: like svar");
            } else {
                log.info("Pdl/tps person: avvik {}", finnAvvik(fraPDL, personFraTps));
            }
        } catch (Exception e) {
            log.info("Pdl/tps person feil", e);
        }
    }

    private String finnAvvik(Person pdl, Person tps) {
        String navn = Objects.equals(pdl.getNavn(), tps.getNavn()) ? "" : " navn ";
        String aktørid = Objects.equals(tps.getAktørId(), pdl.getAktørId()) ? "" : " aktørId ";
        String fødselsnummer = Objects.equals(tps.getFødselsnummer(), pdl.getFødselsnummer()) ? "" : " fødselsnummer ";
        return "Avvik" + navn + aktørid + fødselsnummer;

    }

    private static String navn(List<Navn> navn) {
        return navn.stream()
                .map(PdlTjenesteImpl::navn)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fant ikke navn"));
    }

    private static String navn(Navn navn) {
        if (navn.getForkortetNavn() != null) {
            log.info("Pdl navn: bruker forkortet navn");
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
