package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto;

import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDto;

import java.util.List;

public record SaksbehandlereOgSaksbehandlerGrupper(List<SaksbehandlerDto> saksbehandlere, List<SaksbehandlerGruppeDto> saksbehandlerGrupper) {
}
