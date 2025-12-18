package no.nav.foreldrepenger.los.statistikk.kø;

import jakarta.persistence.Embeddable;

@Embeddable
public record KøType(Long koeId, Long tidsstempel) {
}
