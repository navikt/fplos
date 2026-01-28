package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SorteringDto;
import no.nav.foreldrepenger.los.statistikk.AktiveOgTilgjenglige;

public record SakslisteDto(Long sakslisteId,
                           String navn,
                           SorteringDto sortering,
                           List<BehandlingType> behandlingTyper,
                           List<FagsakYtelseType> fagsakYtelseTyper,
                           List<AndreKriterierDto> andreKriterier,
                           @NotNull List<KøSorteringFeltDto> sorteringTyper,
                           List<String> saksbehandlerIdenter,
                           StatistikkDto gjeldendeStatistikk) {

    public SakslisteDto(OppgaveFiltrering of, AktiveOgTilgjenglige aktiveOgTilgjenglige) {
        var statistikk = aktiveOgTilgjenglige != null ? new StatistikkDto(aktiveOgTilgjenglige.aktive(), aktiveOgTilgjenglige.tilgjengelige(), aktiveOgTilgjenglige.ventende()) : null;
        this(of.getId(), of.getNavn(), new SorteringDto(of), of.getBehandlingTyper(), of.getFagsakYtelseTyper(),
            AndreKriterierDto.listeFra(of.getFiltreringAndreKriterierTyper()), KøSorteringFeltDto.alle(), saksbehandlerIdenter(of.getSaksbehandlere()), statistikk);
    }

    private static List<String> saksbehandlerIdenter(List<Saksbehandler> saksbehandlere) {
        return saksbehandlere.stream().map(Saksbehandler::getSaksbehandlerIdent).toList();
    }

    public record StatistikkDto(int alleOppgaver, int tilgjengeligeOppgaver, int behandlingerPåVent) {
    }
}
