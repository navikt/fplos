package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OppgaverForFørsteStønadsdag(@JsonProperty("forsteStonadsdag") LocalDate førsteStønadsdag, Long antall) {
}
