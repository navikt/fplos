package no.nav.fplos.person;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.vedtak.exception.FunksjonellException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PersonTjeneste {

    private static final Logger log = LoggerFactory.getLogger(PersonTjeneste.class);

    private TpsAdapter tpsAdapter;
    private PdlTjeneste pdlTjeneste;

    PersonTjeneste() {
        // for CDI proxy
    }

    @Inject
    public PersonTjeneste(TpsAdapter tpsAdapter, PdlTjeneste pdlTjeneste) {
        this.tpsAdapter = tpsAdapter;
        this.pdlTjeneste = pdlTjeneste;
    }

    public Optional<Person> hentPerson(AktørId aktørId) {
        var pdlPerson = hent(aktørId);
        //pdlPerson.ifPresent(this::verifiserMedTps);
        return pdlPerson;
    }

    private Optional<Person> hent(AktørId aktørId) {
        try {
            return pdlTjeneste.hentPerson(aktørId);
        } catch (FunksjonellException e) {
            if (e.getFeil().getFeilmelding().contains("Error: Person ikke funnet")) {
                return Optional.empty();
            }
            throw e;
        }
    }

    private void verifiserMedTps(Person pdlPerson) {
        var personTps = tpsAdapter.hentPerson(pdlPerson.getAktørId());
        if (personTps.isPresent()) {
            if (Objects.equals(pdlPerson, personTps.get())) {
                log.info("Pdl/tps person: like svar");
            } else {
                log.info("Pdl/tps person: avvik {}", finnAvvik(pdlPerson, personTps.get()));
            }
        } else {
            log.info(("Pdl/tps person: fant ikke person i tps"));
        }
    }

    private String finnAvvik(Person pdl, Person tps) {
        String navn = Objects.equals(pdl.getNavn(), tps.getNavn()) ? "" : " navn ";
        String aktørid = Objects.equals(tps.getAktørId(), pdl.getAktørId()) ? "" : " aktørId ";
        String fødselsnummer = Objects.equals(tps.getFødselsnummer(), pdl.getFødselsnummer()) ? "" : " fødselsnummer ";
        return "Avvik" + navn + aktørid + fødselsnummer;

    }
}
