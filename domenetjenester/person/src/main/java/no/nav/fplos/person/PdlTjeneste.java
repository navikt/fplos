package no.nav.fplos.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;

public interface PdlTjeneste {
    void hentPerson(AktørId aktørId, Person personFraTps);
}
