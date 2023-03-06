package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;

import java.util.List;

public interface FpsakKlient {

    List<NøkkeltallBehandlingVentestatusDto> hentBehandlingVentestatusNøkkeltall();

}
