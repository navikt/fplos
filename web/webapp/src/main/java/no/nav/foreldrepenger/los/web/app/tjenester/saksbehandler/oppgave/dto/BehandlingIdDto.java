package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.Digits;
import java.util.Objects;
import java.util.UUID;

public class BehandlingIdDto implements AbacDto {

    @JsonProperty("behandlingId")
    @Digits(integer = 18, fraction = 0)
    private final Long behandlingId;

    @JsonProperty("uuid")
    private final UUID uuid;

    public BehandlingIdDto() {
        behandlingId = null;
        uuid = null;
    }

    public BehandlingIdDto(String id) {
        this.uuid = UUID.fromString(id);
        if(this.uuid == null) {
            Objects.requireNonNull(id, "behandlingId");
            this.behandlingId = Long.valueOf(id);
        } else {
            this.behandlingId = null;
        }
    }

    public BehandlingIdDto(Long behandlingId) {
        Objects.requireNonNull(behandlingId, "behandlingId");
        this.behandlingId = behandlingId;
        uuid = null;
    }

    public BehandlingIdDto(UUID uuid) {
        behandlingId = null;
        Objects.requireNonNull(uuid, "uuid");
        this.uuid = uuid;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "BehandlingIdDto{" +
                "behandlingId='" + behandlingId + '\'' +
                "uuid='" + uuid + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}

