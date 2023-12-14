package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto;

import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;

import java.util.List;

public record SaksbehandlerGruppeDto(long gruppeId, String gruppeNavn, List<SaksbehandlerMedAvdelingerDto> saksbehandlere) {
}
