package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.loslager.oppgave.KøSortering;

import java.time.LocalDate;

public class SorteringDto {

    private KøSortering sorteringType;
    private Long fomDager;
    private Long tomDager;
    private LocalDate fomDato;
    private LocalDate tomDato;
    private boolean erDynamiskPeriode;

    public SorteringDto(KøSortering sorteringType, Long fomDager, Long tomDager, LocalDate fomDato, LocalDate tomDato, boolean erDynamiskPeriode) {
        this.sorteringType = sorteringType;
        this.fomDager = fomDager;
        this.tomDager = tomDager;
        this.fomDato = fomDato;
        this.tomDato = tomDato;
        this.erDynamiskPeriode = erDynamiskPeriode;
    }

    public KøSortering getSorteringType() {
        return sorteringType;
    }

    public Long getFomDager() {
        return fomDager;
    }

    public Long getTomDager() {
        return tomDager;
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
