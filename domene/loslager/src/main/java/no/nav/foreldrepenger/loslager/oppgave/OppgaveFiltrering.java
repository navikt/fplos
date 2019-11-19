package no.nav.foreldrepenger.loslager.oppgave;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;

@Entity(name = "OppgaveFiltrering")
@Table(name = "OPPGAVE_FILTRERING")
public class OppgaveFiltrering extends BaseEntitet{
    @Id
    @SequenceGenerator(name="my_seq", sequenceName="SEQ_OPPGAVE_FILTRERING", allocationSize = 200)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_seq")
    private Long id;

    @Column(name = "navn", updatable = false)
    private String navn;

    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "sortering", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + KøSortering.DISCRIMINATOR + "'"))
    private KøSortering sortering;

    @OneToMany(mappedBy = "oppgaveFiltrering")
    private List<FiltreringBehandlingType> filtreringBehandlingTyper = new ArrayList<>();

    @OneToMany(mappedBy = "oppgaveFiltrering")
    private List<FiltreringYtelseType> filtreringYtelseTyper = new ArrayList<>();

    @OneToMany(mappedBy = "oppgaveFiltrering")
    private List<FiltreringAndreKriterierType> andreKriterierTyper = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "AVDELING_ID")
    private Avdeling avdeling;

    @Column(name = "AVDELING_ID", updatable = false, insertable = false)
    private Long avdelingId;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "ER_DYNAMISK_PERIODE")
    private boolean erDynamiskPeriode = Boolean.FALSE;

    @Column(name = "FOM_DATO")
    private LocalDate fomDato;

    @Column(name = "TOM_DATO")
    private LocalDate tomDato;

    @Column(name = "FOM_DAGER")
    private Long fomDager;

    @Column(name = "TOM_DAGER")
    private Long tomDager;

    @ManyToMany
    @JoinTable(name = "FILTRERING_SAKSBEHANDLER",
            joinColumns = {@JoinColumn(name = "OPPGAVE_FILTRERING_ID")},
            inverseJoinColumns = {@JoinColumn(name = "SAKSBEHANDLER_ID")})
    private List<Saksbehandler> saksbehandlere = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public KøSortering getSortering() {
        return sortering;
    }

    public List<FiltreringBehandlingType> getFiltreringBehandlingTyper() {
        return filtreringBehandlingTyper;
    }

    public List<FiltreringYtelseType> getFiltreringYtelseTyper() {
        return filtreringYtelseTyper;
    }

    public List<FiltreringAndreKriterierType> getFiltreringAndreKriterierTyper() {
        return andreKriterierTyper;
    }

    public Avdeling getAvdeling() {
        return avdeling;
    }

    public boolean getErDynamiskPeriode() {
        return erDynamiskPeriode;
    }

    public LocalDate getFomDato() {
        return fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }

    public Long getFomDager() {
        return fomDager;
    }

    public Long getTomDager() {
        return tomDager;
    }

    public static OppgaveFiltrering nyTomOppgaveFiltrering(Avdeling avdeling){
        return new Builder()
                .medAvdeling(avdeling)
                .medNavn("Ny liste")
                .medSortering(KøSortering.BEHANDLINGSFRIST)
                .build();
    }

    public static OppgaveFiltrering.Builder builder(){
        return new OppgaveFiltrering.Builder();
    }

    public List<Saksbehandler> getSaksbehandlere() {
        return Collections.unmodifiableList(saksbehandlere);
    }

    public void leggTilSaksbehandler(Saksbehandler saksbehandler){
        saksbehandlere.add(saksbehandler);
    }

    public void fjernSaksbehandler(Saksbehandler saksbehandler){
        saksbehandlere.remove(saksbehandler);
    }

    public static class Builder {
        private OppgaveFiltrering tempOppgaveFiltrering;

        private Builder() {
            tempOppgaveFiltrering = new OppgaveFiltrering();
        }

        public Builder medNavn(String navn){
            tempOppgaveFiltrering.navn = navn;
            return this;
        }

        public Builder medSortering(KøSortering køSortering){
            tempOppgaveFiltrering.sortering = køSortering;
            return this;
        }

        public Builder medAvdeling(Avdeling avdeling){
            tempOppgaveFiltrering.avdeling = avdeling;
            return this;
        }

        public Builder medErDynamiskPeriode(boolean erDynamiskPeriode){
            tempOppgaveFiltrering.erDynamiskPeriode = erDynamiskPeriode;
            return this;
        }

        public Builder medFomDato(LocalDate fomDato){
            tempOppgaveFiltrering.fomDato = fomDato;
            return this;
        }

        public Builder medTomDato(LocalDate tomDato){
            tempOppgaveFiltrering.tomDato = tomDato;
            return this;
        }

        public Builder medFomDager(Long fomDager){
            tempOppgaveFiltrering.fomDager = fomDager;
            return this;
        }

        public Builder medTomDager(Long tomDager){
            tempOppgaveFiltrering.tomDager = tomDager;
            return this;
        }

        public OppgaveFiltrering build(){
            OppgaveFiltrering oppgaveFiltrering = tempOppgaveFiltrering;
            tempOppgaveFiltrering = new OppgaveFiltrering();
            return oppgaveFiltrering;
        }

    }

}
