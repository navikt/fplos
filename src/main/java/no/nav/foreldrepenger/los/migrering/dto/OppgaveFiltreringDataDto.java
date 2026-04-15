package no.nav.foreldrepenger.los.migrering.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

public record OppgaveFiltreringDataDto(
    Long id,
    String navn,
    String beskrivelse,
    KøSortering køSortering,
    String avdelingId,
    LocalDate fomDato,
    LocalDate tomDato,
    Long fomDager,
    Long tomDager,
    Periodefilter periodeFilter,
    String opprettetAv,
    LocalDateTime opprettetTidspunkt,
    String endretAv,
    LocalDateTime endretTidspunkt,
    List<BehandlingType> behandlingTyper,
    List<FagsakYtelseType> fagsakYtelseTyper,
    List<AndreKriterierDataDto> andreKriterier,
    Set<String> saksbehandlerIdenter) {}
