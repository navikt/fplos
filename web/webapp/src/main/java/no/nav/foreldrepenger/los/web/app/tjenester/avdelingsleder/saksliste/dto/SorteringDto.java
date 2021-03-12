package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

public class SorteringDto {

    private KøSortering sorteringType;
    private Long fra;
    private Long til;
    private LocalDate fomDato;
    private LocalDate tomDato;
    private boolean erDynamiskPeriode;

    public SorteringDto(KøSortering sorteringType,
                        Long fra,
                        Long til,
                        LocalDate fomDato,
                        LocalDate tomDato,
                        boolean erDynamiskPeriode) {
        this.sorteringType = sorteringType;
        this.fra = fra;
        this.til = til;
        this.fomDato = fomDato;
        this.tomDato = tomDato;
        this.erDynamiskPeriode = erDynamiskPeriode;
    }

    public KøSortering getSorteringType() {
        return sorteringType;
    }

    public Long getFra() {
        return fra;
    }

    public Long getTil() {
        return til;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    public boolean isErDynamiskPeriode() {
        return erDynamiskPeriode;
    }
}
