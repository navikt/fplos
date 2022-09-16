package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import java.util.List;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;

public interface FpsakKlient  {

    List<NøkkeltallBehandlingVentestatusDto> hentBehandlingVentestatusNøkkeltall();

}
