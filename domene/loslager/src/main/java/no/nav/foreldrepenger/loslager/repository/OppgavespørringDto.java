package no.nav.foreldrepenger.loslager.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringBehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;

public class OppgavespørringDto {
    private KøSortering sortering;
    private Long id;
    private List<BehandlingType> behandlingTyper;
    private List<FagsakYtelseType> ytelseTyper;
    private List<AndreKriterierType> inkluderAndreKriterierTyper;
    private List<AndreKriterierType> ekskluderAndreKriterierTyper;
    private boolean erDynamiskPeriode;
    private LocalDate filtrerFomDato;
    private LocalDate filtrerTomDato;
    private Long filtrerFomDager;
    private Long filtrerTomDager;

    public OppgavespørringDto(OppgaveFiltrering oppgaveFiltrering){
         sortering = oppgaveFiltrering.getSortering();
         id = oppgaveFiltrering.getAvdeling().getId();
         behandlingTyper = behandlingTypeFra(oppgaveFiltrering);
         ytelseTyper = ytelseType(oppgaveFiltrering);
         inkluderAndreKriterierTyper = inkluderAndreKriterierTyperFra(oppgaveFiltrering);
         ekskluderAndreKriterierTyper = ekskluderAndreKriterierTyperFra(oppgaveFiltrering);
         erDynamiskPeriode = oppgaveFiltrering.getErDynamiskPeriode();
         filtrerFomDato = oppgaveFiltrering.getFomDato();
         filtrerTomDato = oppgaveFiltrering.getTomDato();
         filtrerFomDager = oppgaveFiltrering.getFomDager();
         filtrerTomDager = oppgaveFiltrering.getTomDager();
    }

    public OppgavespørringDto(Long id, KøSortering sortering, List<BehandlingType> behandlingTyper,
                              List<FagsakYtelseType> ytelseTyper, List<AndreKriterierType> inkluderAndreKriterierTyper,
                              List<AndreKriterierType> ekskluderAndreKriterierTyper, boolean erDynamiskPeriode,
                              LocalDate filtrerFomDato, LocalDate filtrerTomDato, Long filtrerFomDager, Long filtrerTomDager) {
        this.sortering = sortering;
        this.id = id;
        this.behandlingTyper = behandlingTyper;
        this.ytelseTyper = ytelseTyper;
        this.inkluderAndreKriterierTyper = inkluderAndreKriterierTyper;
        this.ekskluderAndreKriterierTyper = ekskluderAndreKriterierTyper;
        this.erDynamiskPeriode = erDynamiskPeriode;
        this.filtrerFomDato = filtrerFomDato;
        this.filtrerTomDato = filtrerTomDato;
        this.filtrerFomDager = filtrerFomDager;
        this.filtrerTomDager = filtrerTomDager;
    }

    public KøSortering getSortering() {
        return sortering;
    }

    public Long getId() {
        return id;
    }

    public List<BehandlingType> getBehandlingTyper() {
        return behandlingTyper;
    }

    public List<FagsakYtelseType> getYtelseTyper() {
        return ytelseTyper;
    }

    public List<AndreKriterierType> getInkluderAndreKriterierTyper() {
        return inkluderAndreKriterierTyper;
    }

    public List<AndreKriterierType> getEkskluderAndreKriterierTyper() {
        return ekskluderAndreKriterierTyper;
    }

    public boolean isErDynamiskPeriode() {
        return erDynamiskPeriode;
    }

    public LocalDate getFiltrerFomDato() {
        return filtrerFomDato;
    }

    public LocalDate getFiltrerTomDato() {
        return filtrerTomDato;
    }

    public Long getFiltrerFomDager() {
        return filtrerFomDager;
    }

    public Long getFiltrerTomDager() {
        return filtrerTomDager;
    }

    private List<AndreKriterierType> ekskluderAndreKriterierTyperFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringAndreKriterierTyper().stream()
                .filter(FiltreringAndreKriterierType::isEkskluder)
                .map(FiltreringAndreKriterierType::getAndreKriterierType)
                .collect(Collectors.toList());
    }

    private List<AndreKriterierType> inkluderAndreKriterierTyperFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringAndreKriterierTyper().stream()
                .filter(FiltreringAndreKriterierType::isInkluder)
                .map(FiltreringAndreKriterierType::getAndreKriterierType)
                .collect(Collectors.toList());
    }

    private List<FagsakYtelseType> ytelseType(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringYtelseTyper().stream()
                .map(FiltreringYtelseType::getFagsakYtelseType)
                .collect(Collectors.toList());
    }

    private List<BehandlingType> behandlingTypeFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringBehandlingTyper().stream()
                .map(FiltreringBehandlingType::getBehandlingType)
                .collect(Collectors.toList());
    }
}
