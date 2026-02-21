package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringAndreKriterierType;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.statistikk.KøStatistikkDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SorteringDto;

public record SakslisteDto(@NotNull Long sakslisteId,
                           @NotNull String navn,
                           @NotNull SorteringDto sortering,
                           @NotNull @Size(max = 20) List<BehandlingType> behandlingTyper,
                           @NotNull @Size(max = 20) List<FagsakYtelseType> fagsakYtelseTyper,
                           @NotNull AndreKriterieDto andreKriterie,
                           @NotNull List<KøSorteringFeltDto> sorteringTyper,
                           @NotNull List<SaksbehandlerDto> saksbehandlere,
                           StatistikkDto gjeldendeStatistikk) {

    public SakslisteDto(OppgaveFiltrering of, List<SaksbehandlerDto> saksbehandlere, KøStatistikkDto køStatistikk) {
        var statistikk = køStatistikk != null ? new StatistikkDto(køStatistikk.aktive(), køStatistikk.tilgjengelige(), køStatistikk.ventende()) : null;
        this(of.getId(), of.getNavn(), new SorteringDto(of), of.getBehandlingTyper(), of.getFagsakYtelseTyper(),
            AndreKriterieDto.fra(of.getFiltreringAndreKriterierTyper()),
            KøSorteringFeltDto.alle(), saksbehandlere, statistikk);
    }

    public record StatistikkDto(@NotNull int alleOppgaver, @NotNull int tilgjengeligeOppgaver, int behandlingerPåVent) {
    }

    public record AndreKriterieDto(Set<AndreKriterierType> inkluder, Set<AndreKriterierType> ekskluder) {

        static AndreKriterieDto fra(List<FiltreringAndreKriterierType> filtreringAndreKriterierTyper) {
            var inkluder = filtreringAndreKriterierTyper.stream()
                .filter(FiltreringAndreKriterierType::isInkluder)
                .map(FiltreringAndreKriterierType::getAndreKriterierType)
                .collect(java.util.stream.Collectors.toSet());

            var ekskluder = filtreringAndreKriterierTyper.stream()
                .filter(fakt -> !fakt.isInkluder())
                .map(FiltreringAndreKriterierType::getAndreKriterierType)
                .collect(java.util.stream.Collectors.toSet());

            return new AndreKriterieDto(inkluder, ekskluder);
        }
    }
}
