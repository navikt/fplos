package no.nav.fplos.person;

import java.util.Random;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;

public final class FiktivTestPerson {

    public static Person nyPerson() {
        Fødselsnummer fnr = new FiktiveFnr().nesteFødselsnummer();
        AktørId aktørId = new AktørId(Long.valueOf(fnr.asValue()));
        return new Person.Builder()
                .medFnr(fnr)
                .medNavn(navn())
                .build();
    }

    private static String navn() {
        int uppercaseA = 65; // A
        int uppercaseZ = 90; // Z
        int navnLengde = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(navnLengde);
        for (int i = 0; i < navnLengde; i++) {
            int randomUppercaseAsciiInt = uppercaseA +
                    (int) (random.nextFloat() * (uppercaseZ - uppercaseA + 1));
            buffer.append((char) randomUppercaseAsciiInt);
        }
        return buffer.toString();
    }
}
