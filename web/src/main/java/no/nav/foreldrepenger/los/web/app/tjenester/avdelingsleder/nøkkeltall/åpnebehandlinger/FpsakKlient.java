package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import java.util.List;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingFørsteUttakDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentefristUtløperDto;

public interface FpsakKlient {

    List<NøkkeltallBehandlingFørsteUttakDto> hentBehandlingFørsteUttakNøkkeltall();

    List<NøkkeltallBehandlingVentefristUtløperDto> hentVentefristerNøkkeltall();

}
