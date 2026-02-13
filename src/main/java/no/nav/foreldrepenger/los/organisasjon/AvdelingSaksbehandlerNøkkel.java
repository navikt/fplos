package no.nav.foreldrepenger.los.organisasjon;

import jakarta.persistence.Embeddable;

@Embeddable
public record AvdelingSaksbehandlerNøkkel(Saksbehandler saksbehandler, Avdeling avdeling) {
}
