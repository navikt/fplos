package no.nav.foreldrepenger.los.oppgavekø;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;


@Entity(name = "OppgaveFiltrering")
@Table(name = "OPPGAVE_FILTRERING")
public class OppgaveFiltrering extends BaseEntitet {
    @Id
    @SequenceGenerator(name = "my_seq", sequenceName = "SEQ_OPPGAVE_FILTRERING", allocationSize = 200)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_seq")
    private Long id;

    @Column(name = "navn", updatable = false)
    private String navn;

    @Column(name = "sortering", updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private KøSortering sortering;

    @OneToMany(mappedBy = "oppgaveFiltrering", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FiltreringBehandlingType> filtreringBehandlingTyper = new HashSet<>();

    @OneToMany(mappedBy = "oppgaveFiltrering", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FiltreringYtelseType> filtreringYtelseTyper = new HashSet<>();

    @OneToMany(mappedBy = "oppgaveFiltrering", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FiltreringAndreKriterierType> andreKriterierTyper = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "AVDELING_ID")
    private Avdeling avdeling;

    @Column(name = "AVDELING_ID", updatable = false, insertable = false)
    private Long avdelingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERIODEFILTER_TYPE")
    private Periodefilter periodefilter = Periodefilter.FAST_PERIODE;

    @Column(name = "FOM_DATO")
    private LocalDate fomDato;

    @Column(name = "TOM_DATO")
    private LocalDate tomDato;

    @Column(name = "FOM_DAGER")
    private Long fra;

    @Column(name = "TOM_DAGER")
    private Long til;

    @ManyToMany
    @JoinTable(name = "FILTRERING_SAKSBEHANDLER", joinColumns = {@JoinColumn(name = "OPPGAVE_FILTRERING_ID")}, inverseJoinColumns = {@JoinColumn(name = "SAKSBEHANDLER_ID")})
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
        return filtreringBehandlingTyper.stream().toList();
    }

    public List<BehandlingType> getBehandlingTyper() {
        return filtreringBehandlingTyper.stream().map(FiltreringBehandlingType::getBehandlingType).toList();
    }

    public List<FagsakYtelseType> getFagsakYtelseTyper() {
        return filtreringYtelseTyper.stream().map(FiltreringYtelseType::getFagsakYtelseType).toList();
    }

    public List<FiltreringAndreKriterierType> getFiltreringAndreKriterierTyper() {
        return andreKriterierTyper.stream().toList();
    }

    public Set<FiltreringAndreKriterierType> getSet() {
        return andreKriterierTyper;
    }

    public Avdeling getAvdeling() {
        return avdeling;
    }

    public Periodefilter getPeriodefilter() {
        return periodefilter;
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

    public static OppgaveFiltrering nyTomOppgaveFiltrering(Avdeling avdeling) {
        return new Builder().medAvdeling(avdeling).medNavn("Ny liste").medSortering(KøSortering.BEHANDLINGSFRIST).build();
    }

    public List<Saksbehandler> getSaksbehandlere() {
        return Collections.unmodifiableList(saksbehandlere);
    }

    public void leggTilSaksbehandler(Saksbehandler saksbehandler) {
        if (!saksbehandlere.contains(saksbehandler)) {
            saksbehandlere.add(saksbehandler);
        }
    }

    public void fjernSaksbehandler(Saksbehandler saksbehandler) {
        this.saksbehandlere.remove(saksbehandler);
    }

    public void leggTilFilter(FiltreringAndreKriterierType filtreringAndreKriterierType) {
        fjernFilter(filtreringAndreKriterierType.getAndreKriterierType());
        this.andreKriterierTyper.add(filtreringAndreKriterierType);
    }

    public void leggTilFilter(FagsakYtelseType fagsakYtelseType) {
        fjernFilter(fagsakYtelseType);
        this.filtreringYtelseTyper.add(new FiltreringYtelseType(this, fagsakYtelseType));
    }

    public void leggTilFilter(BehandlingType behandlingType) {
        fjernFilter(behandlingType);
        this.filtreringBehandlingTyper.add(new FiltreringBehandlingType(this, behandlingType));
    }

    public void fjernFilter(AndreKriterierType andreKriterierType) {
        this.andreKriterierTyper.removeIf(akt -> akt.getAndreKriterierType() == andreKriterierType);
    }

    public void fjernFilter(FagsakYtelseType fagsakYtelseType) {
        this.filtreringYtelseTyper.removeIf(yt -> yt.getFagsakYtelseType() == fagsakYtelseType);
    }

    public void fjernFilter(BehandlingType behandlingType) {
        this.filtreringBehandlingTyper.removeIf(fbt -> fbt.getBehandlingType() == behandlingType);
    }

    public static OppgaveFiltrering.Builder builder() {
        return new OppgaveFiltrering.Builder();
    }

    public static class Builder {

        private OppgaveFiltrering tempOppgaveFiltrering;

        private Builder() {
            tempOppgaveFiltrering = new OppgaveFiltrering();
        }

        public Builder medNavn(String navn) {
            tempOppgaveFiltrering.navn = navn;
            return this;
        }

        public Builder medSortering(KøSortering køSortering) {
            tempOppgaveFiltrering.sortering = køSortering;
            return this;
        }

        public Builder medAvdeling(Avdeling avdeling) {
            tempOppgaveFiltrering.avdeling = avdeling;
            return this;
        }

        public Builder medFomDato(LocalDate fomDato) {
            tempOppgaveFiltrering.fomDato = fomDato;
            return this;
        }

        public Builder medTomDato(LocalDate tomDato) {
            tempOppgaveFiltrering.tomDato = tomDato;
            return this;
        }

        public Builder medFraVerdi(Long fra) {
            tempOppgaveFiltrering.fra = fra;
            return this;
        }

        public Builder medTilVerdi(Long til) {
            tempOppgaveFiltrering.til = til;
            return this;
        }

        public OppgaveFiltrering build() {
            var oppgaveFiltrering = tempOppgaveFiltrering;
            tempOppgaveFiltrering = new OppgaveFiltrering();
            return oppgaveFiltrering;
        }

    }

}
