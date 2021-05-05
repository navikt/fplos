package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;


public record FagsakMedPersonDto(String saksnummerString, FagsakYtelseType fagsakYtelseType,
                                 FagsakStatus status, PersonDto person, LocalDate barnFødt) {

    public Long getSaksnummer() {
        return Long.parseLong(saksnummerString);
    }
}
