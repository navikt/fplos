package no.nav.foreldrepenger.los.oppgavekø;

import java.time.LocalDate;
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


@Entity(name = "OppgaveFiltrering")
@Table(name = "OPPGAVE_FILTRERING")
public class OppgaveFiltrering extends BaseEntitet {
    @Id
    @SequenceGenerator(name = "my_seq", sequenceName = "SEQ_OPPGAVE_FILTRERING", allocationSize = 200)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_seq")
    private Long id;

    @Column(name = "navn")
    private String navn;

    @Column(name = "sortering", nullable = false)
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

    public Long getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public KøSortering getSortering() {
        return sortering;
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

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setAvdeling(Avdeling avdeling) {
        this.avdeling = avdeling;
    }

    public void setSortering(KøSortering sortering) {
        this.sortering = sortering;
    }

    public void setFiltreringBehandlingTyper(Set<BehandlingType> behandlingTyper) {
        this.filtreringBehandlingTyper.clear();
        behandlingTyper.stream()
            .map(type -> new FiltreringBehandlingType(this, type))
            .forEach(f -> this.filtreringBehandlingTyper.add(f));
    }

    public void setFiltreringYtelseTyper(Set<FagsakYtelseType> fagsakYtelseTyper) {
        this.filtreringYtelseTyper.clear();
        fagsakYtelseTyper.stream()
            .map(type -> new FiltreringYtelseType(this, type))
            .forEach(f -> this.filtreringYtelseTyper.add(f));
    }

    public void setAndreKriterierTyper(Set<AndreKriterierType> inkluder, Set<AndreKriterierType> ekskluder) {
        this.andreKriterierTyper.clear();
        inkluder.stream()
            .map(type -> new FiltreringAndreKriterierType(this, type, true))
            .forEach(a -> this.andreKriterierTyper.add(a));
        ekskluder.stream()
            .map(type -> new FiltreringAndreKriterierType(this, type, false))
            .forEach(a -> this.andreKriterierTyper.add(a));
    }

    public void setPeriodefilter(Periodefilter periodefilter) {
        this.periodefilter = periodefilter;
    }

    public void setFomDato(LocalDate fomDato) {
        this.fomDato = fomDato;
    }

    public void setTomDato(LocalDate tomDato) {
        this.tomDato = tomDato;
    }

    public void setFra(Long fra) {
        this.fra = fra;
    }

    public void setTil(Long til) {
        this.til = til;
    }

}
