package no.nav.foreldrepenger.los.tjenester.felles.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record SaksbehandlerDto(@NotNull SaksbehandlerBrukerIdentDto brukerIdent,
                               String navn,
                               String ansattAvdeling) {

    @Override
    public String toString() {
        return "SaksbehandlerDto{" + "brukerIdent=" + brukerIdent + '}';
    }

    @JsonProperty("brukerIdent")
    public String getBrukerIdent() {
        return brukerIdent.getVerdi();
    }
}
