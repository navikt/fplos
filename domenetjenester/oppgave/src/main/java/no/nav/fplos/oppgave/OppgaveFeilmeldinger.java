package no.nav.fplos.oppgave;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface OppgaveFeilmeldinger extends DeklarerteFeil { // NOSONAR
    OppgaveFeilmeldinger FACTORY = FeilFactory.create(OppgaveFeilmeldinger.class);

    @TekniskFeil(feilkode = "FP-442142", feilmelding = "Fant ingen ident for aktør %s.", logLevel = LogLevel.WARN)
    Feil identIkkeFunnet(AktørId aktoerId);
}
