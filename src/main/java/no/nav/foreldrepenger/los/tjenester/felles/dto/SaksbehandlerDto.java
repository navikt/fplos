package no.nav.foreldrepenger.los.tjenester.felles.dto;

import jakarta.validation.constraints.NotNull;

public record SaksbehandlerDto(@NotNull String brukerIdent,
                               @NotNull String navn,
                               String ansattAvdeling) {

    @Override
    public String toString() {
        return "SaksbehandlerDto{" + "brukerIdent=" + brukerIdent + '}';
    }

}
