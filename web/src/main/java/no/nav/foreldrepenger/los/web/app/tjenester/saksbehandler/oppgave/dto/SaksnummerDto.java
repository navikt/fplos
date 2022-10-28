package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record SaksnummerDto(@NotNull @Pattern(regexp = "^[0-9_]+$") String saksnummer) {
}
