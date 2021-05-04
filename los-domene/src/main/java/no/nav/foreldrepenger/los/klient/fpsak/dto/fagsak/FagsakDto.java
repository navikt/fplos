package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.FagsakStatus;

public record FagsakDto(String aktoerId, String aktørId, String saksnummer, FagsakYtelseTypeDto fagsakYtelseType,
                        FagsakStatus status, LocalDate barnFodt, LocalDate barnFødt) {

    public String aktørId() {
        return aktørId == null ? aktoerId : aktørId;
    }

    public LocalDate barnFødt() {
        return barnFødt == null ? barnFodt : barnFødt;
    }

    @Override
    public String toString() {
        return "FagsakDto{" + "saksnummer='" + saksnummer + '\'' + ", fagsakYtelseType=" + fagsakYtelseType
                + ", status=" + status + ", barnFodt=" + barnFodt + ", barnFødt=" + barnFødt + '}';
    }
}
