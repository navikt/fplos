package no.nav.foreldrepenger.los.statistikk.kø;

import jakarta.persistence.Embeddable;

@Embeddable
public record StatistikkOppgaveFilterType(Long oppgaveFilterId, Long tidsstempel) {
}
