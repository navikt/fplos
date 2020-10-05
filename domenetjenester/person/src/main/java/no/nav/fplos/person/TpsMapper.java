package no.nav.fplos.person;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personidenter;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;

public final class TpsMapper {

    private TpsMapper() {
        //for å hindre instanser av util klasse
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

    public static HentPersonRequest hentPersonRequest(Fødselsnummer fødselsnummer) {
        var request = new HentPersonRequest();
        request.setAktoer(lagTpsPersonIdent(fødselsnummer.asValue()));
        return request;
    }

    private static PersonIdent lagTpsPersonIdent(String fnr) {
        if (fnr == null || fnr.isEmpty()) {
            throw new IllegalArgumentException("Fødselsnummer kan ikke være null eller tomt");
        }

        PersonIdent personIdent = new PersonIdent();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(fnr);

        Personidenter type = new Personidenter();
        type.setValue(erDNr(fnr) ? "DNR" : "FNR");
        norskIdent.setType(type);

        personIdent.setIdent(norskIdent);
        return personIdent;
    }

    private static boolean erDNr(String fnr) {
        //D-nummer kan indentifiseres ved at første siffer er 4 større enn hva som finnes i fødselsnumre
        char førsteTegn = fnr.charAt(0);
        return førsteTegn >= '4' && førsteTegn <= '7';
    }
}
