package no.nav.fplos.kafkatjenester;

import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;

import java.util.UUID;

class TestUtil {

    static BehandlingFpsak.Builder behandlingBuilderMal() {
        return BehandlingFpsak.builder()
                .medUuid(UUID.nameUUIDFromBytes("TEST".getBytes()))
                .medBehandlendeEnhetNavn("NAV")
                .medAnsvarligSaksbehandler("VLLOS")
                .medStatus("-")
                .medHarGradering(null)
                .medHarRefusjonskravFraArbeidsgiver(null);
    }
}
