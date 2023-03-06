package no.nav.foreldrepenger.los.oppgavekø;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "OppgaveFiltreringOppdaterer")
@Table(name = "OPPGAVE_FILTRERING")
public class OppgaveFiltreringOppdaterer extends BaseEntitet {
    @Id
    private Long id;

    @Column(name = "navn")
    private String navn;

    @Column(name = "sortering")
    private String sortering;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "ER_DYNAMISK_PERIODE")
    private boolean erDynamiskPeriode = Boolean.FALSE;

    @Column(name = "FOM_DATO")
    private LocalDate fomDato;

    @Column(name = "TOM_DATO")
    private LocalDate tomDato;

    @Column(name = "FOM_DAGER")
    private Long fra;

    @Column(name = "TOM_DAGER")
    private Long til;

    public OppgaveFiltreringOppdaterer endreNavn(String nyttNavn) {
        this.navn = nyttNavn;
        return this;
    }

    public OppgaveFiltreringOppdaterer endreSortering(String sorteringskode) {
        sortering = sorteringskode;
        return this;
    }

    public OppgaveFiltreringOppdaterer endreErDynamiskPeriode(boolean erDynamiskPeriode) {
        this.erDynamiskPeriode = erDynamiskPeriode;
        return this;
    }

    public OppgaveFiltreringOppdaterer endreFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
        return this;
    }

    public OppgaveFiltreringOppdaterer endreTomDato(LocalDate tomDato) {
        this.tomDato = tomDato;
        return this;
    }

    public OppgaveFiltreringOppdaterer endreFraVerdi(Long fra) {
        this.fra = fra;
        return this;
    }

    public OppgaveFiltreringOppdaterer endreTilVerdi(Long til) {
        this.til = til;
        return this;
    }

}
