package no.nav.foreldrepenger.los.migrering.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OppgaveDataDto(
    Long id,
    UUID behandlingId,
    String behandlendeEnhet,
    boolean aktiv,
    LocalDateTime oppgaveAvsluttet,
    String opprettetAv,
    LocalDateTime opprettetTidspunkt,
    String endretAv,
    LocalDateTime endretTidspunkt,
    ReservasjonDataDto reservasjonDataDto,
    List<OppgaveEgenskapDataDto> oppgaveEgenskaper
) {
}
