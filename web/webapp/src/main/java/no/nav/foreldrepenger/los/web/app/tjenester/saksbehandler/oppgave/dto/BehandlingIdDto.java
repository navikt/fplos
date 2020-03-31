package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.NotNull;

public class BehandlingIdDto implements AbacDto {

    @JsonProperty("behandlingId")
    @NotNull
    private final BehandlingId value;

    @JsonCreator
    public BehandlingIdDto(String value) {
        this.value = BehandlingId.fromString(value);
    }

    @JsonIgnore
    public BehandlingId getValue() {
        return value;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

