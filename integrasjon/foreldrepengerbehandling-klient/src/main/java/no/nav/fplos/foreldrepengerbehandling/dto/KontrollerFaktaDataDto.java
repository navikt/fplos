package no.nav.fplos.foreldrepengerbehandling.dto;

import java.util.List;

public class KontrollerFaktaDataDto {

    private List<KontrollerFaktaPeriodeDto> perioder;

    public void setPerioder(List<KontrollerFaktaPeriodeDto> perioder) {
        this.perioder = perioder;
    }

    public List<KontrollerFaktaPeriodeDto> getPerioder() {
        return perioder;
    }
}
