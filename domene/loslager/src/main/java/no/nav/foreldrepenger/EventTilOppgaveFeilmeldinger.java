package no.nav.foreldrepenger;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface EventTilOppgaveFeilmeldinger extends DeklarerteFeil { // NOSONAR
    EventTilOppgaveFeilmeldinger FACTORY = FeilFactory.create(EventTilOppgaveFeilmeldinger.class);

    @TekniskFeil(feilkode = "FPLOS-666", feilmelding = "Mer enn en aktiv oppgave med behandlingId %s.", logLevel = LogLevel.WARN)
    Feil merEnnEnOppgave(Long behandlingId);

    @TekniskFeil(feilkode = "FPLOS-901", feilmelding = "Ukjent %s: %s.", logLevel = LogLevel.WARN)
    Feil ukjentEnum(String enumNavn, String kode);
}
