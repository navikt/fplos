package no.nav.fplos.avdelingsleder;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.IntegrasjonFeil;

public interface AvdelingslederSaksbehandlerTjenesteFeil extends DeklarerteFeil {

    AvdelingslederSaksbehandlerTjenesteFeil FACTORY = FeilFactory.create(AvdelingslederSaksbehandlerTjenesteFeil.class);

    @IntegrasjonFeil(feilkode = "FPLOS-737", feilmelding = "Feil i kall mot grensesnittet %s", logLevel = LogLevel.WARN)
    Feil feil(String tjeneste, Exception e);

}

