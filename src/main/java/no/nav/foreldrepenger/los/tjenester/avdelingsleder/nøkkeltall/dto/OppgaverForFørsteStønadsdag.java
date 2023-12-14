package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OppgaverForFørsteStønadsdag(@JsonProperty("forsteStonadsdag") LocalDate førsteStønadsdag, Long antall) {
}
