package no.nav.foreldrepenger.los.tjenester.avdelingsleder.reservasjon;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

public record AvdelingReservasjonDto(@NotNull LocalDateTime reservertTilTidspunkt, @NotNull String reservertAvIdent, @NotNull String reservertAvNavn,
                                     @NotNull Long oppgaveId, @NotNull String oppgaveSaksNr, @NotNull BehandlingType behandlingType,
                                     @NotNull FagsakYtelseType ytelseType, @NotNull Set<AndreKriterierType> andreKriterier) {

    public AvdelingReservasjonDto(Reservasjon reservasjon, String reservertAvNavn) {
        this(reservasjon.getReservertTil(), reservasjon.getReservertAv(), reservertAvNavn,
            reservasjon.getOppgave().getId(), reservasjon.getOppgave().getSaksnummer().getVerdi(),
            reservasjon.getOppgave().getBehandlingType(), reservasjon.getOppgave().getFagsakYtelseType(),
            reservasjon.getOppgave().getOppgaveEgenskaper().stream().map(OppgaveEgenskap::getAndreKriterierType).collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return "AvdelingReservasjonDto{" + "reservertTilTidspunkt=" + reservertTilTidspunkt + ", reservertAvIdent='" + reservertAvIdent + '\''
            + ", oppgaveSaksNr='" + oppgaveSaksNr + '\'' + ", ytelseType=" + ytelseType + ", behandlingType=" + behandlingType + '}';
    }
}
