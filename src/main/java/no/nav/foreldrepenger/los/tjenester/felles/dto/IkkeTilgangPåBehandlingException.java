package no.nav.foreldrepenger.los.tjenester.felles.dto;


import no.nav.foreldrepenger.los.domene.typer.BehandlingId;

public class IkkeTilgangPåBehandlingException extends RuntimeException {

    public IkkeTilgangPåBehandlingException(BehandlingId behandlingId) {
        super("Ikke tilgang på behandling " + behandlingId);
    }
}
