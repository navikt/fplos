package no.nav.foreldrepenger.los.migrering.dto;

import java.time.LocalDateTime;

public record SaksbehandlerGruppeDataDto(
    Long id,
    String gruppeNavn,
    String avdelingId,
    String opprettetAv,
    LocalDateTime opprettetTidspunkt,
    String endretAv,
    LocalDateTime endretTidspunkt
) {}
