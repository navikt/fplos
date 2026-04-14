package no.nav.foreldrepenger.los.migrering.dto;

import java.time.LocalDateTime;

public record SaksbehandlerDataDto(
    String saksbehandlerIdent,
    String navn,
    String ansattVedEnhet,
    String opprettetAv,
    LocalDateTime opprettetTidspunkt,
    String endretAv,
    LocalDateTime endretTidspunkt
) {}
