package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record SaksbehandlereOgSaksbehandlerGrupper(@NotNull List<SaksbehandlerGruppeDto> saksbehandlerGrupper) {
}
