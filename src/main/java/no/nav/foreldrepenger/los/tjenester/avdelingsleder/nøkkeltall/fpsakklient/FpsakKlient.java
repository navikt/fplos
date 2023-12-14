package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.fpsakklient;

import java.util.List;

import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingFørsteUttakDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingVentefristUtløperDto;

public interface FpsakKlient {

    List<NøkkeltallBehandlingFørsteUttakDto> hentBehandlingFørsteUttakNøkkeltall();

    List<NøkkeltallBehandlingVentefristUtløperDto> hentVentefristerNøkkeltall();

}
