package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

public record BehandlingÅrsakDto(BehandlingÅrsakType behandlingArsakType,
                                 BehandlingÅrsakType behandlingÅrsakType) {

    public BehandlingÅrsakType behandlingÅrsakType() {
        return behandlingÅrsakType == null ? behandlingArsakType : behandlingÅrsakType;
    }
}
