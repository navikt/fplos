package no.nav.foreldrepenger.loslager.oppgave;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "OppgaveFiltrering")
@Table(name = "OPPGAVE_FILTRERING")
public class OppgaveFiltrering extends BaseEntitet{
    @Id
    @SequenceGenerator(name="my_seq", sequenceName="SEQ_OPPGAVE_FILTRERING", allocationSize = 200)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_seq")
    private Long id;

    @Column(name = "navn", updatable = false)
    private String navn;

    @Column(name = "sortering", updatable = false)
    @Convert(converter = KøSortering.KodeverdiConverter.class)
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
    private Long fra;

    @Column(name = "TOM_DAGER")
    private Long til;

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

    public Long getFra() {
        return fra;
    }

    public Long getTil() {
        return til;
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

        public Builder medFraVerdi(Long fra){
            tempOppgaveFiltrering.fra = fra;
            return this;
        }

        public Builder medTilVerdi(Long til){
            tempOppgaveFiltrering.til = til;
            return this;
        }

        public OppgaveFiltrering build(){
            OppgaveFiltrering oppgaveFiltrering = tempOppgaveFiltrering;
            tempOppgaveFiltrering = new OppgaveFiltrering();
            return oppgaveFiltrering;
        }

    }

}
