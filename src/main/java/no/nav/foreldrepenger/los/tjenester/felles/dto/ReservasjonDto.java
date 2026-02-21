package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

public record ReservasjonDto(@NotNull LocalDateTime reservertTilTidspunkt, @NotNull String reservertAvIdent, @NotNull String reservertAvNavn,
                             LocalDateTime flyttetTidspunkt, String flyttetAvIdent, String flyttetAvNavn, String begrunnelse, @NotNull Long oppgaveId,
                             @NotNull String oppgaveSaksNr, @NotNull BehandlingType behandlingType) {

    public ReservasjonDto(Reservasjon reservasjon, String reservertAvNavn, String navnFlyttetAv) {
        this(reservasjon.getReservertTil(), reservasjon.getReservertAv(), reservertAvNavn, reservasjon.getFlyttetTidspunkt(),
            reservasjon.getFlyttetAv(), navnFlyttetAv, reservasjon.getBegrunnelse(), reservasjon.getOppgave().getId(),
            reservasjon.getOppgave().getSaksnummer().getVerdi(), reservasjon.getOppgave().getBehandlingType());
    }

    @Override
    public String toString() {
        return "ReservasjonDto{" + "reservertTilTidspunkt=" + reservertTilTidspunkt + ", reservertAvIdent='" + reservertAvIdent + '\''
            + ", flyttetTidspunkt=" + flyttetTidspunkt + ", flyttetAvIdent='" + flyttetAvIdent + '\'' + ", oppgaveId=" + oppgaveId
            + ", oppgaveSaksNr=" + oppgaveSaksNr + ", behandlingType=" + behandlingType + '}';
    }
}
