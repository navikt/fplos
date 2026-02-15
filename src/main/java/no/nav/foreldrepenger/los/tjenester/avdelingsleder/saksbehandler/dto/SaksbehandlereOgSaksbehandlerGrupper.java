package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDto;

public record SaksbehandlereOgSaksbehandlerGrupper(@NotNull List<SaksbehandlerDto> saksbehandlere,
                                                   @NotNull List<SaksbehandlerGruppeDto> saksbehandlerGrupper) {
}
