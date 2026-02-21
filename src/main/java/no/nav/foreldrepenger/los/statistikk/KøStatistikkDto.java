package no.nav.foreldrepenger.los.statistikk;

import java.time.LocalDateTime;

public record KøStatistikkDto(LocalDateTime tidspunkt, int aktive, int tilgjengelige, int ventende, int avsluttet) {
}
