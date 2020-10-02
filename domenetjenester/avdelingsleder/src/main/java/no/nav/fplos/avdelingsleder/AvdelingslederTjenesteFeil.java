package no.nav.fplos.avdelingsleder;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.FunksjonellFeil;

public interface AvdelingslederTjenesteFeil extends DeklarerteFeil {

    AvdelingslederTjenesteFeil FACTORY = FeilFactory.create(AvdelingslederTjenesteFeil.class);

    @FunksjonellFeil(feilkode = "FP-164687", feilmelding = "Fant ikke oppgavekø med id %s", løsningsforslag = "Last siden på nytt", logLevel = LogLevel.INFO)
    Feil fantIkkeOppgavekø(Long oppgaveFilterId);
}
