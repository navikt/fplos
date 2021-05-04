package no.nav.foreldrepenger.los.web.app.tjenester.abac;

import java.util.Set;

public record PipDto(Set<String> aktørIder, String fagsakStatus, String behandlingStatus) {
}
