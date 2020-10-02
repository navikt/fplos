package no.nav.fplos.oppgave;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.FunksjonellFeil;

public interface OppgaveTjenesteFeil extends DeklarerteFeil {

    OppgaveTjenesteFeil FACTORY = FeilFactory.create(OppgaveTjenesteFeil.class);

    @FunksjonellFeil(feilkode = "FP-164687", feilmelding = "Fant ikke oppgavekø", løsningsforslag = "Last siden på nytt, forsøk igjen.", logLevel = LogLevel.INFO)
    Feil fantIkkeOppgavekø(Long oppgaveFilterId);
}
