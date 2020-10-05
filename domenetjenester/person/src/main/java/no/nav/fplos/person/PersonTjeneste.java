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

    PersonTjeneste() {
        // for CDI proxy
    }

    @Inject
    public PersonTjeneste(TpsAdapter tpsAdapter) {
        this.tpsAdapter = tpsAdapter;
    }

    public Optional<Person> hentPerson(Fødselsnummer fnr) {
        return tpsAdapter.hentPerson(fnr);
    }

    public Optional<Person> hentPerson(AktørId aktørId) {
        return tpsAdapter.hentPerson(aktørId);
    }
}
