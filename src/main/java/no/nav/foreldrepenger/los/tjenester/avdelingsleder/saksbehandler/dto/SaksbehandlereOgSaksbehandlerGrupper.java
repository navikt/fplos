package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto;

import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;

import java.util.List;

public record SaksbehandlereOgSaksbehandlerGrupper(List<SaksbehandlerMedAvdelingerDto> saksbehandlere, List<SaksbehandlerGruppeDto> saksbehandlerGrupper) {
}
