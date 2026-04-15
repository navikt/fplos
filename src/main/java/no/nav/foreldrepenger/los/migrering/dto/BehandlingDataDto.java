package no.nav.foreldrepenger.los.migrering.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingTilstand;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.SaksnummerDto;

public record BehandlingDataDto(
    UUID id,
    SaksnummerDto saksnummer,
    AktørId aktørId,
    Fagsystem kildeSystem,
    FagsakYtelseType fagsakYtelseType,
    BehandlingType behandlingType,
    BehandlingTilstand behandlingTilstand,
    String aktiveAksjonspunkt,
    LocalDateTime ventefrist,
    LocalDateTime opprettet,
    LocalDateTime avsluttet,
    LocalDate behandlingsfrist,
    LocalDate førsteStønadsdag,
    BigDecimal feilutbetalingBelop,
    LocalDate feilutbetalingStart,
    String behandlendeEnhet,
    String opprettetAv,
    LocalDateTime opprettetTidspunkt,
    String endretAv,
    LocalDateTime endretTidspunkt,
    Set<AndreKriterierType> egenskaper
) {
}

