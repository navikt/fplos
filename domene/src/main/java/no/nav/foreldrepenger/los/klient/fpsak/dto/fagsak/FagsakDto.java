package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record FagsakDto(String saksnummer, FagsakYtelseType fagsakYtelseType, String aktørId) {

    @Override
    public String toString() {
        return "FagsakDto{" + "saksnummer='" + saksnummer + '\'' + ", fagsakYtelseType=" + fagsakYtelseType + '}';
    }
}
