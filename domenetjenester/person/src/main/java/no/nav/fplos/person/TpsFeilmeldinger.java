package no.nav.fplos.person;

import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.ManglerTilgangFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

import static no.nav.vedtak.feil.LogLevel.ERROR;
import static no.nav.vedtak.feil.LogLevel.WARN;

public interface TpsFeilmeldinger extends DeklarerteFeil {

    TpsFeilmeldinger FACTORY = FeilFactory.create(TpsFeilmeldinger.class);

    @TekniskFeil(feilkode = "FP-164686", feilmelding = "Person er ikke Bruker, kan ikke hente ut brukerinformasjon", logLevel = LogLevel.WARN)
    Feil ukjentBrukerType();

    @ManglerTilgangFeil(feilkode = "FP-432142", feilmelding = "TPS ikke tilgjengelig (sikkerhetsbegrensning)", logLevel = ERROR)
    Feil tpsUtilgjengeligSikkerhetsbegrensning(HentPersonSikkerhetsbegrensning cause);

    @TekniskFeil(feilkode = "FP-715013", feilmelding = "Fant ikke person i TPS", logLevel = WARN)
    Feil fantIkkePerson(HentPersonPersonIkkeFunnet cause);

    @TekniskFeil(feilkode = "FP-181235", feilmelding = "Fant ikke aktørId i TPS", logLevel = WARN)
    Feil fantIkkePersonForAktørId();

    @ManglerTilgangFeil(feilkode = "FP-115180", feilmelding = "TPS ikke tilgjengelig (sikkerhetsbegrensning)", logLevel = ERROR)
    Feil tpsUtilgjengeligGeografiskTilknytningSikkerhetsbegrensing(HentGeografiskTilknytningSikkerhetsbegrensing cause);

    @TekniskFeil(feilkode = "FP-349049", feilmelding = "Fant ikke geografisk informasjon for person", logLevel = WARN)
    Feil geografiskTilknytningIkkeFunnet(HentGeografiskTilknytningPersonIkkeFunnet cause);

}
