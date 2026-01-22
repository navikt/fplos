package no.nav.foreldrepenger.los.statistikk;

import java.time.LocalDateTime;

public record AktiveOgTilgjenglige(LocalDateTime tidspunkt, int aktive, int tilgjengelige) {
}
