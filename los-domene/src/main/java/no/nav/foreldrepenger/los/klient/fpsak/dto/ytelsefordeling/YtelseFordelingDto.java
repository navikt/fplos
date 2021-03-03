package no.nav.foreldrepenger.los.klient.fpsak.dto.ytelsefordeling;

import no.nav.foreldrepenger.los.klient.fpsak.dto.periode.PeriodeDto;

import java.time.LocalDate;
import java.util.List;

public class YtelseFordelingDto {
    private List<PeriodeDto> ikkeOmsorgPerioder;
    private List<PeriodeDto> aleneOmsorgPerioder;
    private AnnenforelderHarRettDto annenforelderHarRettDto;
    private LocalDate endringsdato;
    private int gjeldendeDekningsgrad;
    private LocalDate førsteUttaksdato;

    private YtelseFordelingDto() {
    }

    public List<PeriodeDto> getIkkeOmsorgPerioder() {
        return ikkeOmsorgPerioder;
    }

    public List<PeriodeDto> getAleneOmsorgPerioder() {
        return aleneOmsorgPerioder;
    }

    public LocalDate getEndringsdato() {
        return endringsdato;
    }

    public AnnenforelderHarRettDto getAnnenforelderHarRettDto() {
        return annenforelderHarRettDto;
    }

    public int getGjeldendeDekningsgrad() {
        return gjeldendeDekningsgrad;
    }

    public LocalDate getFørsteUttaksdato() {
        return førsteUttaksdato;
    }

    public void setIkkeOmsorgPerioder(List<PeriodeDto> ikkeOmsorgPerioder) {
        this.ikkeOmsorgPerioder = ikkeOmsorgPerioder;
    }

    public void setAleneOmsorgPerioder(List<PeriodeDto> aleneOmsorgPerioder) {
        this.aleneOmsorgPerioder = aleneOmsorgPerioder;
    }

    public void setAnnenforelderHarRettDto(AnnenforelderHarRettDto annenforelderHarRettDto) {
        this.annenforelderHarRettDto = annenforelderHarRettDto;
    }

    public void setEndringsdato(LocalDate endringsdato) {
        this.endringsdato = endringsdato;
    }

    public void setGjeldendeDekningsgrad(int gjeldendeDekningsgrad) {
        this.gjeldendeDekningsgrad = gjeldendeDekningsgrad;
    }

    public void setFørsteUttaksdato(LocalDate førsteUttaksdato) {
        this.førsteUttaksdato = førsteUttaksdato;
    }
}
