package no.nav.foreldrepenger.los.klient.fpsak;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.vedtak.exception.ManglerTilgangException;

public class InternIdMappingException extends ManglerTilgangException {

    public InternIdMappingException(BehandlingId eksternBehandlingId) {
        super("FP-981171", "Ikke tilgang til behandling " + eksternBehandlingId);
    }
}
