package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class BehandlingIdDto implements AbacDto {

    @JsonProperty("uuid")
    @NotNull
    @Digits(integer = 18, fraction = 0)
    private final UUID uuid;

    public BehandlingIdDto() {
        uuid = null; // NOSONAR
    }

    public BehandlingIdDto(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        this.uuid = uuid;
    }

    public BehandlingIdDto(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }


    public UUID getVerdi() {
        return uuid;
    }

    @Override
    public String toString() {
        return "BehandlingIdDto{" +
                "uuid='" + uuid + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}