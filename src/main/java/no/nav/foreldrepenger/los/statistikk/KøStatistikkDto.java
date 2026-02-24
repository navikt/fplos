package no.nav.foreldrepenger.los.statistikk;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record KøStatistikkDto(@NotNull LocalDateTime tidspunkt, @NotNull int aktive, @NotNull int tilgjengelige, @NotNull int ventende,
                              @NotNull int avsluttet) {
}
