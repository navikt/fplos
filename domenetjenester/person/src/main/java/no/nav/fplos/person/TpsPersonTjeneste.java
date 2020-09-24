package no.nav.fplos.person;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;

@ApplicationScoped
public class TpsPersonTjeneste implements PersonTjeneste {

    private TpsAdapter tpsAdapter;

    TpsPersonTjeneste() {
        // for CDI proxy
    }

    @Inject
    public TpsPersonTjeneste(TpsAdapter tpsAdapter) {
        this.tpsAdapter = tpsAdapter;
    }

    @Override
    public Optional<Person> hentPerson(Fødselsnummer fnr) {
        return tpsAdapter.hentAktørForFødselsnummer(fnr).flatMap(tpsAdapter::hentPerson);
    }

    @Override
    public Optional<Person> hentPerson(AktørId aktørId) {
        return tpsAdapter.hentPerson(aktørId);
    }
}
