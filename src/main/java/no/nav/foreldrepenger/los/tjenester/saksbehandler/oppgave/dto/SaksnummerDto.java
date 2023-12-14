package no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SaksnummerDto(@NotNull @Pattern(regexp = "^[0-9_]+$") String saksnummer) {
}
