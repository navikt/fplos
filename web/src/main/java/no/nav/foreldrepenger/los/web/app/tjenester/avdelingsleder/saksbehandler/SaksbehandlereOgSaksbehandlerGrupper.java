package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler;

import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;

import java.util.List;

public record SaksbehandlereOgSaksbehandlerGrupper(List<SaksbehandlerMedAvdelingerDto> saksbehandlere, List<SaksbehandlerGruppeDto> saksbehandlerGrupper) {
}
