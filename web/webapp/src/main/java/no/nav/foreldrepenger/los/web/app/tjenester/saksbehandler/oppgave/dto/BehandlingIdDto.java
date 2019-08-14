package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import java.util.Objects;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class BehandlingIdDto implements AbacDto {

    @JsonProperty("behandlingId")
    @NotNull
    @Digits(integer = 18, fraction = 0)
    private final Long behandlingId;

    public BehandlingIdDto() {
        behandlingId = null; // NOSONAR
    }

    public BehandlingIdDto(Long behandlingId) {
        Objects.requireNonNull(behandlingId, "behandlingId");
        this.behandlingId = behandlingId;
    }

    public BehandlingIdDto(String behandlingId) {
        this.behandlingId = Long.valueOf(behandlingId);
    }


    public Long getVerdi() {
        return behandlingId;
    }

    @Override
    public String toString() {
        return "BehandlingIdDto{" +
                "behandlingId='" + behandlingId + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}