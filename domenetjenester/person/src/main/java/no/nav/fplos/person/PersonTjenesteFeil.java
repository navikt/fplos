package no.nav.fplos.person;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface PersonTjenesteFeil extends DeklarerteFeil {

    PersonTjenesteFeil FACTORY = FeilFactory.create(PersonTjenesteFeil.class);

    @TekniskFeil(feilkode = "FP-164686", feilmelding = "Person er ikke Bruker, kan ikke hente ut brukerinformasjon", logLevel = LogLevel.WARN)
    Feil ukjentBrukerType();

    @TekniskFeil(feilkode = "FP-715013", feilmelding = "Fant ikke person i TPS", logLevel = LogLevel.WARN)
    Feil fantIkkePerson(Exception cause);

}
