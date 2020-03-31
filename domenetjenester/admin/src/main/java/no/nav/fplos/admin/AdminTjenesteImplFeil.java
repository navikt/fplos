package no.nav.fplos.admin;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;
import no.nav.vedtak.felles.jpa.TomtResultatException;

public interface AdminTjenesteImplFeil extends DeklarerteFeil {
    AdminTjenesteImplFeil FACTORY = FeilFactory.create(AdminTjenesteImplFeil.class);

    @TekniskFeil(feilkode = "FPLOS-999", feilmelding = "Finner ikke feilet event med id %s", logLevel = LogLevel.WARN, exceptionClass = TomtResultatException.class)
    Feil finnerIkkeFeiletEvent(Long eventId);
}
