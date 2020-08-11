package no.nav.fplos.foreldrepengerbehandling;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public class InternIdMappingException extends ManglerTilgangException {

    public InternIdMappingException(BehandlingId eksternBehandlingId) {
        super(FpBehandlingRestKlientFeil.FACTORY.ikkeTilgangTilInternId(eksternBehandlingId));
    }

    private interface FpBehandlingRestKlientFeil extends DeklarerteFeil {
        FpBehandlingRestKlientFeil FACTORY = FeilFactory.create(FpBehandlingRestKlientFeil.class);

        @TekniskFeil(feilkode = "FP-981171", feilmelding = "Ikke tilgang til behandling %s", logLevel = LogLevel.WARN)
        Feil ikkeTilgangTilInternId(BehandlingId eksternBehandlingId);
    }
}
