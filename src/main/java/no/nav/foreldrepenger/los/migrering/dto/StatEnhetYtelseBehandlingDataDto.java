package no.nav.foreldrepenger.los.migrering.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record StatEnhetYtelseBehandlingDataDto(
    String behandlendeEnhet,
    Long tidsstempel,
    FagsakYtelseType fagsakYtelseType,
    BehandlingType behandlingType,
    LocalDate statistikkDato,
    Integer antallAktive,
    Integer antallOpprettet,
    Integer antallAvsluttet
) {}
