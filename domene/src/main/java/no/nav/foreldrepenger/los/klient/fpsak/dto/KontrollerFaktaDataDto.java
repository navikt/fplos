package no.nav.foreldrepenger.los.klient.fpsak.dto;

import java.util.List;

public record KontrollerFaktaDataDto(List<KontrollerFaktaPeriodeDto> perioder) {

    public List<KontrollerFaktaPeriodeDto> perioder() {
        return perioder == null ? List.of() : perioder;
    }
}
