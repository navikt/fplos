package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;

import java.util.Arrays;
import java.util.Optional;

public enum OppgaveEgenskapTypeMapper {
    EGENSKAP_ENDRINGSSØKNAD(AndreKriterierType.ENDRINGSSØKNAD) {
        @Override
        public boolean erEgenskapAktuell(BehandlingFpsak behandling) {
            return behandling.erEndringssøknad();
        }
    },
    EGENSKAP_BERØRTBEHANDLING(AndreKriterierType.BERØRT_BEHANDLING) {
        @Override
        public boolean erEgenskapAktuell(BehandlingFpsak behandling) {
            return behandling.erBerørtBehandling();
        }
    };

    private final AndreKriterierType type;

    OppgaveEgenskapTypeMapper(AndreKriterierType type) {
        this.type = type;
    }

    public AndreKriterierType getType() {
        return type;
    }

    public static Optional<OppgaveEgenskapTypeMapper> tilTypeMapper(AndreKriterierType type) {
        return Arrays.stream(values())
                .filter(m -> m.type.equals(type))
                .findFirst();
    }

    public abstract boolean erEgenskapAktuell(BehandlingFpsak behandlingFpsak);
}
