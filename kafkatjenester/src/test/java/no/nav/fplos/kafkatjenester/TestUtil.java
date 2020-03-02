package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;

class TestUtil {

    static BehandlingFpsak.Builder behandlingBuilderMal() {
        return BehandlingFpsak.builder()
                .medBehandlingId(BehandlingId.random())
                .medBehandlendeEnhetNavn("NAV")
                .medAnsvarligSaksbehandler("VLLOS")
                .medStatus("-")
                .medHarGradering(null)
                .medHarRefusjonskravFraArbeidsgiver(null);
    }
}
