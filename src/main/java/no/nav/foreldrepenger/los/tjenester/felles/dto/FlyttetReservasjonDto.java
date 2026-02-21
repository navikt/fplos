package no.nav.foreldrepenger.los.tjenester.felles.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FlyttetReservasjonDto(@NotNull LocalDateTime tidspunkt, @NotNull String flyttetAvIdent, @NotNull String navn,
                                    @NotNull String begrunnelse) {
}
