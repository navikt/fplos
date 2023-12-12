package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler;

import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;
import java.util.List;

public record SaksbehandlerGruppeDto(long gruppeId, String gruppeNavn, List<SaksbehandlerMedAvdelingerDto> saksbehandlere) {
}
