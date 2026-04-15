package no.nav.foreldrepenger.los.migrering.dto;

import java.time.LocalDateTime;

public record ReservasjonDataDto(
    LocalDateTime reservertTil,
    String reservertAv,
    String flyttetAv,
    LocalDateTime flyttetTidspunkt,
    String begrunnelse,
    String opprettetAv,
    LocalDateTime opprettetTidspunkt,
    String endretAv,
    LocalDateTime endretTidspunkt
) {
}
