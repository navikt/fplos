package no.nav.fplos.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;

public final class TpsPersonMapper {

    TpsPersonMapper() {
    }

    public static Person tilPerson(AktørId aktørId, Bruker bruker) {
        String navn = bruker.getPersonnavn().getSammensattNavn();
        Aktoer aktoer = bruker.getAktoer();
        PersonIdent pi = (PersonIdent) aktoer;
        Fødselsnummer ident = new Fødselsnummer(pi.getIdent().getIdent());
        return new Person.Builder()
            .medAktørId(aktørId)
            .medFnr(ident)
            .medNavn(navn)
            .build();
    }
}
