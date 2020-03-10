package no.nav.fplos.person;

import static no.nav.vedtak.feil.LogLevel.WARN;

import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface TpsFeilmeldinger extends DeklarerteFeil {

    TpsFeilmeldinger FACTORY = FeilFactory.create(TpsFeilmeldinger.class);

    @TekniskFeil(feilkode = "FP-164686", feilmelding = "Person er ikke Bruker, kan ikke hente ut brukerinformasjon", logLevel = LogLevel.WARN)
    Feil ukjentBrukerType();

    @TekniskFeil(feilkode = "FP-715013", feilmelding = "Fant ikke person i TPS", logLevel = WARN)
    Feil fantIkkePerson(HentPersonPersonIkkeFunnet cause);

}
