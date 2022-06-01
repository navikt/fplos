package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringBehandlingType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;

public class Oppgavespørring {
    private final KøSortering sortering;
    private final Long enhetId;
    private final List<BehandlingType> behandlingTyper;
    private final List<FagsakYtelseType> ytelseTyper;
    private final List<AndreKriterierType> inkluderAndreKriterierTyper;
    private final List<AndreKriterierType> ekskluderAndreKriterierTyper;
    private final boolean erDynamiskPeriode;
    private final LocalDate filtrerFomDato;
    private final LocalDate filtrerTomDato;
    private final Long filtrerFra;
    private final Long filtrerTil;
    private boolean forAvdelingsleder;
    private Long avgrenseTilOppgaveId;
    private boolean ignorerReserversjoner;

    public Oppgavespørring(OppgaveFiltrering oppgaveFiltrering) {
        sortering = oppgaveFiltrering.getSortering();
        enhetId = oppgaveFiltrering.getAvdeling().getId();
        behandlingTyper = behandlingTypeFra(oppgaveFiltrering);
        ytelseTyper = ytelseType(oppgaveFiltrering);
        inkluderAndreKriterierTyper = inkluderAndreKriterierTyperFra(oppgaveFiltrering);
        ekskluderAndreKriterierTyper = ekskluderAndreKriterierTyperFra(oppgaveFiltrering);
        erDynamiskPeriode = oppgaveFiltrering.getErDynamiskPeriode();
        filtrerFomDato = oppgaveFiltrering.getFomDato();
        filtrerTomDato = oppgaveFiltrering.getTomDato();
        filtrerFra = oppgaveFiltrering.getFra();
        filtrerTil = oppgaveFiltrering.getTil();
    }

    public Oppgavespørring(Long enhetId,
                           KøSortering sortering,
                           List<BehandlingType> behandlingTyper,
                           List<FagsakYtelseType> ytelseTyper,
                           List<AndreKriterierType> inkluderAndreKriterierTyper,
                           List<AndreKriterierType> ekskluderAndreKriterierTyper,
                           boolean erDynamiskPeriode,
                           LocalDate filtrerFomDato,
                           LocalDate filtrerTomDato,
                           Long filtrerFra,
                           Long filtrerTil) {
        this.sortering = sortering;
        this.enhetId = enhetId;
        this.behandlingTyper = behandlingTyper;
        this.ytelseTyper = ytelseTyper;
        this.inkluderAndreKriterierTyper = inkluderAndreKriterierTyper;
        this.ekskluderAndreKriterierTyper = ekskluderAndreKriterierTyper;
        this.erDynamiskPeriode = erDynamiskPeriode;
        this.filtrerFomDato = filtrerFomDato;
        this.filtrerTomDato = filtrerTomDato;
        this.filtrerFra = filtrerFra;
        this.filtrerTil = filtrerTil;
    }

    public boolean ignorerReserversjoner() {
        return ignorerReserversjoner;
    }

    public void setIgnorerReserversjoner(boolean ignorerReserversjoner) {
        this.ignorerReserversjoner = ignorerReserversjoner;
    }

    public void setForAvdelingsleder(boolean forAvdelingsleder) {
        this.forAvdelingsleder = forAvdelingsleder;
    }

    public Oppgavespørring setAvgrensTilOppgaveId(Long oppgaveId) {
        this.avgrenseTilOppgaveId = oppgaveId;
        return this;
    }

    public boolean getForAvdelingsleder() {
        return forAvdelingsleder;
    }

    public KøSortering getSortering() {
        return sortering;
    }

    public Long getEnhetId() {
        return enhetId;
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

    public Long getFiltrerFra() {
        return filtrerFra;
    }

    public Long getFiltrerTil() {
        return filtrerTil;
    }

    public Optional<Long> getAvgrenseTilOppgaveId() {
        return Optional.ofNullable(avgrenseTilOppgaveId);
    }

    private List<AndreKriterierType> ekskluderAndreKriterierTyperFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringAndreKriterierTyper()
                .stream()
                .filter(FiltreringAndreKriterierType::isEkskluder)
                .map(FiltreringAndreKriterierType::getAndreKriterierType)
                .collect(Collectors.toList());
    }

    private List<AndreKriterierType> inkluderAndreKriterierTyperFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringAndreKriterierTyper()
                .stream()
                .filter(FiltreringAndreKriterierType::isInkluder)
                .map(FiltreringAndreKriterierType::getAndreKriterierType)
                .collect(Collectors.toList());
    }

    private List<FagsakYtelseType> ytelseType(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringYtelseTyper()
                .stream()
                .map(FiltreringYtelseType::getFagsakYtelseType)
                .collect(Collectors.toList());
    }

    private List<BehandlingType> behandlingTypeFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringBehandlingTyper()
                .stream()
                .map(FiltreringBehandlingType::getBehandlingType)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Oppgavespørring{" + "sortering=" + sortering + ", enhetId=" + enhetId + ", behandlingTyper="
                + behandlingTyper + ", ytelseTyper=" + ytelseTyper + ", inkluderAndreKriterierTyper="
                + inkluderAndreKriterierTyper + ", ekskluderAndreKriterierTyper=" + ekskluderAndreKriterierTyper
                + ", erDynamiskPeriode=" + erDynamiskPeriode + ", filtrerFomDato=" + filtrerFomDato
                + ", filtrerTomDato=" + filtrerTomDato + ", filtrerFra=" + filtrerFra + ", filtrerTil=" + filtrerTil
                + ", forAvdelingsleder=" + forAvdelingsleder + ", avgrenseTilOppgaveId=" + avgrenseTilOppgaveId + '}';
    }
}
