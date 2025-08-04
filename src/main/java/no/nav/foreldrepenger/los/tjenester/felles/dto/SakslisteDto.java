package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.List;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SorteringDto;

public record SakslisteDto(SakslisteIdDto sakslisteId,
                           String navn,
                           SorteringDto sortering,
                           List<BehandlingType> behandlingTyper,
                           List<FagsakYtelseType> fagsakYtelseTyper,
                           List<AndreKriterierDto> andreKriterier,
                           List<String> saksbehandlerIdenter) {

    public SakslisteDto(OppgaveFiltrering of) {
        this(new SakslisteIdDto(of.getId()), of.getNavn(), new SorteringDto(of), of.getBehandlingTyper(), of.getFagsakYtelseTyper(),
            AndreKriterierDto.listeFra(of.getFiltreringAndreKriterierTyper()), saksbehandlerIdenter(of.getSaksbehandlere()));
    }

    private static List<String> saksbehandlerIdenter(List<Saksbehandler> saksbehandlere) {
        return saksbehandlere.stream().map(Saksbehandler::getSaksbehandlerIdent).toList();
    }

}
