package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.los.oppgavekø.KøSortering;

import java.time.LocalDate;

public class SorteringDto {

    private final KøSortering sorteringType;
    private final Long fra;
    private final Long til;
    private final LocalDate fomDato;
    private final LocalDate tomDato;
    private final boolean erDynamiskPeriode;

    public SorteringDto(KøSortering sorteringType, Long fra, Long til, LocalDate fomDato, LocalDate tomDato, boolean erDynamiskPeriode) {
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
