package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;

import static java.util.function.Predicate.not;

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
    private boolean ignorerReserversjoner;
    private Long avgrenseTilOppgaveId;
    private Long maxAntallOppgaver;

    public Oppgavespørring(OppgaveFiltrering oppgaveFiltrering) {
        sortering = oppgaveFiltrering.getSortering();
        enhetId = oppgaveFiltrering.getAvdeling().getId();
        behandlingTyper = oppgaveFiltrering.getBehandlingTyper();
        ytelseTyper = oppgaveFiltrering.getFagsakYtelseTyper();
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

    public void setAvgrensTilOppgaveId(Long oppgaveId) {
        this.avgrenseTilOppgaveId = oppgaveId;
    }

    public void setMaksAntall(int maksAntall) {
        this.maxAntallOppgaver = (long) maksAntall;
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

    public Optional<Long> getMaxAntallOppgaver() {
        return Optional.ofNullable(maxAntallOppgaver);
    }

    private List<AndreKriterierType> ekskluderAndreKriterierTyperFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringAndreKriterierTyper()
            .stream()
            .filter(not(FiltreringAndreKriterierType::isInkluder))
            .map(FiltreringAndreKriterierType::getAndreKriterierType)
            .toList();
    }

    private List<AndreKriterierType> inkluderAndreKriterierTyperFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringAndreKriterierTyper()
            .stream()
            .filter(FiltreringAndreKriterierType::isInkluder)
            .map(FiltreringAndreKriterierType::getAndreKriterierType)
            .toList();
    }

    @Override
    public String toString() {
        return "Oppgavespørring{" + "sortering=" + sortering + ", enhetId=" + enhetId + ", behandlingTyper=" + behandlingTyper + ", ytelseTyper="
            + ytelseTyper + ", inkluderAndreKriterierTyper=" + inkluderAndreKriterierTyper + ", ekskluderAndreKriterierTyper="
            + ekskluderAndreKriterierTyper + ", erDynamiskPeriode=" + erDynamiskPeriode + ", filtrerFomDato=" + filtrerFomDato + ", filtrerTomDato="
            + filtrerTomDato + ", filtrerFra=" + filtrerFra + ", filtrerTil=" + filtrerTil + ", forAvdelingsleder=" + forAvdelingsleder
            + ", avgrenseTilOppgaveId=" + avgrenseTilOppgaveId + '}';
    }
}
