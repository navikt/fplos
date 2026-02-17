package no.nav.foreldrepenger.los.organisasjon;

import jakarta.persistence.Embeddable;

@Embeddable
public record GruppeTilknytningNøkkel(Saksbehandler saksbehandler, SaksbehandlerGruppe gruppe) {
}
