package no.nav.fplos.foreldrepengerbehandling;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.event.Level;

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

    @Override
    public void log(Logger logger) {
        //Default støtter ikke info, må utvide
        if (Objects.equals(this.getFeil().getLogLevel(), Level.INFO)) {
            logger.info(getFeil().toLogString(), this);
        } else {
            super.log(logger);
        }
    }

    private interface FpBehandlingRestKlientFeil extends DeklarerteFeil {
        FpBehandlingRestKlientFeil FACTORY = FeilFactory.create(FpBehandlingRestKlientFeil.class);

        //Vurder å øke til WARN/ERROR når behandlinger ikke saksbehandler har tilgang til ikke dukker opp i oppgavelisten
        @TekniskFeil(feilkode = "FP-981171", feilmelding = "Ikke tilgang til behandling %s", logLevel = LogLevel.INFO)
        Feil ikkeTilgangTilInternId(BehandlingId eksternBehandlingId);
    }
}
