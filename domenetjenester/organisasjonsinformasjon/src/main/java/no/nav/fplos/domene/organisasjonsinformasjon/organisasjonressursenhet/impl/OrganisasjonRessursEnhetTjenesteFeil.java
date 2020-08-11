package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.impl;

import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.OrganisasjonRessursEnhetHentEnhetListeRessursIkkeFunnetException;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.OrganisasjonRessursEnhetUgyldigInputException;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeRessursIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjonressursenhet.v1.HentEnhetListeUgyldigInput;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.IntegrasjonFeil;

public interface OrganisasjonRessursEnhetTjenesteFeil extends DeklarerteFeil {

    OrganisasjonRessursEnhetTjenesteFeil FACTORY = FeilFactory.create(OrganisasjonRessursEnhetTjenesteFeil.class);

    @IntegrasjonFeil(feilkode = "FPLOS-923", feilmelding = "Funksjonell feil i grensesnitt mot %s", logLevel =
            LogLevel.WARN, exceptionClass = OrganisasjonRessursEnhetUgyldigInputException.class)
    Feil ugyldigInput(String tjeneste, HentEnhetListeUgyldigInput årsak);

    @IntegrasjonFeil(feilkode = "FPLOS-067", feilmelding = "Ingen liste over enheter til angitt ressurs funnet i grensesnitt mot %s",
            logLevel = LogLevel.WARN, exceptionClass = OrganisasjonRessursEnhetHentEnhetListeRessursIkkeFunnetException.class)
    Feil ikkeFunnet(String tjeneste, HentEnhetListeRessursIkkeFunnet årsak);

    @IntegrasjonFeil(feilkode = "FPLOS-695", feilmelding = "Feil i kall mot grensesnittet %s", logLevel = LogLevel.WARN)
    Feil feil(String tjeneste, Exception e);

}
