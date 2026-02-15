package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDateTime;

public record FlyttetReservasjonDto(LocalDateTime tidspunkt, String flyttetAvIdent, String uid, String navn, String begrunnelse) {
}
