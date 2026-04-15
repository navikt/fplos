package no.nav.foreldrepenger.los.migrering.dto;

import java.time.LocalDateTime;

public record AvdelingDataDto(
    String avdelingEnhet,
    String navn,
    boolean kreverKode6,
    boolean aktiv,
    String opprettetAv,
    LocalDateTime opprettetTidspunkt,
    String endretAv,
    LocalDateTime endretTidspunkt
) {}
