package no.nav.fplos.kafkatjenester;

import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;

class TestUtil {

    static BehandlingFpsak.Builder behandlingBuilderMal() {
        return BehandlingFpsak.builder()
                .medBehandlendeEnhetNavn("NAV")
                .medAnsvarligSaksbehandler("VLLOS")
                .medStatus("-");
    }
}
