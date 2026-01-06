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
    private final String enhetsnummer;
    private final List<BehandlingType> behandlingTyper;
    private final List<FagsakYtelseType> ytelseTyper;
    private final List<AndreKriterierType> inkluderAndreKriterierTyper;
    private final List<AndreKriterierType> ekskluderAndreKriterierTyper;
    private final boolean erDynamiskPeriode;
    private final LocalDate filtrerFomDato;
    private final LocalDate filtrerTomDato;
    private final Long filtrerFra;
    private final Long filtrerTil;
    private final Filtreringstype filtreringstype;
    private boolean fjernReserverte;
    private boolean fjernBeslutteroppgaverMedSammeSaksbehandler;
    private Long maxAntallOppgaver;

    public Oppgavespørring(OppgaveFiltrering oppgaveFiltrering, Filtreringstype filtreringstype) {
        this(
            oppgaveFiltrering.getAvdeling().getAvdelingEnhet(),
            oppgaveFiltrering.getSortering(),
            oppgaveFiltrering.getBehandlingTyper(),
            oppgaveFiltrering.getFagsakYtelseTyper(),
            inkluderAndreKriterierTyperFra(oppgaveFiltrering),
            ekskluderAndreKriterierTyperFra(oppgaveFiltrering),
            oppgaveFiltrering.getErDynamiskPeriode(),
            oppgaveFiltrering.getFomDato(),
            oppgaveFiltrering.getTomDato(),
            oppgaveFiltrering.getFra(),
            oppgaveFiltrering.getTil(),
            filtreringstype
        );

    }

    public Oppgavespørring(String enhetsnummer,
                           KøSortering sortering,
                           List<BehandlingType> behandlingTyper,
                           List<FagsakYtelseType> ytelseTyper,
                           List<AndreKriterierType> inkluderAndreKriterierTyper,
                           List<AndreKriterierType> ekskluderAndreKriterierTyper,
                           boolean erDynamiskPeriode,
                           LocalDate filtrerFomDato,
                           LocalDate filtrerTomDato,
                           Long filtrerFra,
                           Long filtrerTil,
                           Filtreringstype filtreringstype) {
        this.sortering = sortering;
        this.enhetsnummer = enhetsnummer;
        this.behandlingTyper = behandlingTyper;
        this.ytelseTyper = ytelseTyper;
        this.inkluderAndreKriterierTyper = inkluderAndreKriterierTyper;
        this.ekskluderAndreKriterierTyper = ekskluderAndreKriterierTyper;
        this.erDynamiskPeriode = erDynamiskPeriode;
        this.filtrerFomDato = filtrerFomDato;
        this.filtrerTomDato = filtrerTomDato;
        this.filtrerFra = filtrerFra;
        this.filtrerTil = filtrerTil;
        this.filtreringstype = filtreringstype;
    }

    public void setMaksAntall(int maksAntall) {
        this.maxAntallOppgaver = (long) maksAntall;
    }

    public KøSortering getSortering() {
        return sortering;
    }

    public String getEnhetsnummer() {
        return enhetsnummer;
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

    public Filtreringstype getFiltreringstype() {
        return filtreringstype;
    }

    public Optional<Long> getMaxAntallOppgaver() {
        return Optional.ofNullable(maxAntallOppgaver);
    }

    private static List<AndreKriterierType> ekskluderAndreKriterierTyperFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringAndreKriterierTyper()
            .stream()
            .filter(not(FiltreringAndreKriterierType::isInkluder))
            .map(FiltreringAndreKriterierType::getAndreKriterierType)
            .toList();
    }

    private static List<AndreKriterierType> inkluderAndreKriterierTyperFra(OppgaveFiltrering oppgaveFiltrering) {
        return oppgaveFiltrering.getFiltreringAndreKriterierTyper()
            .stream()
            .filter(FiltreringAndreKriterierType::isInkluder)
            .map(FiltreringAndreKriterierType::getAndreKriterierType)
            .toList();
    }

    @Override
    public String toString() {
        return "Oppgavespørring{" + "sortering=" + sortering + "enhetsnummer=" + enhetsnummer + ", behandlingTyper=" + behandlingTyper + ", ytelseTyper="
            + ytelseTyper + ", inkluderAndreKriterierTyper=" + inkluderAndreKriterierTyper + ", ekskluderAndreKriterierTyper="
            + ekskluderAndreKriterierTyper + ", erDynamiskPeriode=" + erDynamiskPeriode + ", filtrerFomDato=" + filtrerFomDato + ", filtrerTomDato="
            + filtrerTomDato + ", filtrerFra=" + filtrerFra + ", filtrerTil=" + filtrerTil + ", filtreringstype=" + filtreringstype + '}';
    }
}
