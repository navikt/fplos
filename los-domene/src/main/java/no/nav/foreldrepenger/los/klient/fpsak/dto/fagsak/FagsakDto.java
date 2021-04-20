package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakStatus;

public record FagsakDto(String aktoerId, String saksnummer, FagsakYtelseTypeDto fagsakYtelseType,
                        FagsakStatus status, LocalDate barnFodt) {
}
