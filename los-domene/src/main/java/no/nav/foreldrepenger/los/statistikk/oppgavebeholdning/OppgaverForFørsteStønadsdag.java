package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record OppgaverForFørsteStønadsdag(@JsonProperty("forsteStonadsdag") LocalDate førsteStønadsdag, Long antall) { }
