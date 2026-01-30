package no.nav.foreldrepenger.los.oppgavekø;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.Periodefilter;

@Entity(name = "OppgaveFiltreringOppdaterer")
@Table(name = "OPPGAVE_FILTRERING")
public class OppgaveFiltreringOppdaterer extends BaseEntitet {
    @Id
    private Long id;

    @Column(name = "navn")
    private String navn;

    @Column(name = "sortering")
    private String sortering;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERIODEFILTER_TYPE")
    private Periodefilter periodefilter;

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

    public OppgaveFiltreringOppdaterer endrePeriodefilter(Periodefilter periodefilter) {
        this.periodefilter = periodefilter;
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
