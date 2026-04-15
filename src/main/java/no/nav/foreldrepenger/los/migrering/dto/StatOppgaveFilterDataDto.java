package no.nav.foreldrepenger.los.migrering.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.statistikk.kø.InnslagType;

public record StatOppgaveFilterDataDto(
    Long oppgaveFilterId,
    Long tidsstempel,
    LocalDate statistikkDato,
    Integer antallAktive,
    Integer antallTilgjengelige,
    Integer antallVentende,
    Integer antallOpprettet,
    Integer antallAvsluttet,
    InnslagType innslagType
) {}
