package no.nav.foreldrepenger.los.tjenester.admin.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import jakarta.validation.Valid;

import java.util.UUID;

public class EnkelBehandlingIdDto implements AbacDto {
    @Valid
    private final BehandlingId behandlingId;

    @JsonCreator
    public EnkelBehandlingIdDto(@JsonProperty("behandlingId") UUID uuid) {
        behandlingId = BehandlingId.fromUUID(uuid);
    }

    public BehandlingId getBehandlingId() {
        return behandlingId;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
