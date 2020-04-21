package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.loslager.BehandlingId;

public class IkkeTilgangPåBehandlingException extends RuntimeException {

    public IkkeTilgangPåBehandlingException(BehandlingId behandlingId) {
        super("Ikke tilgang på behandling " + behandlingId);
    }
}
