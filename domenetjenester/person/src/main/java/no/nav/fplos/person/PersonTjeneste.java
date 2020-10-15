package no.nav.fplos.person;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;

@ApplicationScoped
public class PersonTjeneste {

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

    public Optional<Person> hentPerson(Fødselsnummer fnr) {
        return tpsAdapter.hentPerson(fnr);
    }

    public Optional<Person> hentPerson(AktørId aktørId) {
        var personTps = tpsAdapter.hentPerson(aktørId);
        personTps.ifPresent(p -> pdlTjeneste.hentPerson(aktørId, p));
        return personTps;
    }
}
