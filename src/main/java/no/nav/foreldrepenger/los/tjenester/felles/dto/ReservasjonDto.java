package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

public record ReservasjonDto(LocalDateTime reservertTilTidspunkt, String reservertAvUid, String reservertAvNavn,
                             LocalDateTime flyttetTidspunkt, String flyttetAv, String flyttetAvNavn, String begrunnelse,
                             Long oppgaveId, String oppgaveSaksNr, BehandlingType behandlingType) {

    public ReservasjonDto(Reservasjon reservasjon, String reservertAvNavn, String navnFlyttetAv) {
        this(reservasjon.getReservertTil(), reservasjon.getReservertAv(), reservertAvNavn,
            reservasjon.getFlyttetTidspunkt(), reservasjon.getFlyttetAv(), navnFlyttetAv, reservasjon.getBegrunnelse(),
            reservasjon.getOppgave().getId(), reservasjon.getOppgave().getSaksnummer().getVerdi(), reservasjon.getOppgave().getBehandlingType());
    }

    @Override
    public String toString() {
        return "ReservasjonDto{" + "reservertTilTidspunkt=" + reservertTilTidspunkt + ", reservertAvUid='" + reservertAvUid + '\''
            + ", flyttetTidspunkt=" + flyttetTidspunkt + ", flyttetAv='" + flyttetAv + '\'' + ", oppgaveId=" + oppgaveId + ", oppgaveSaksNr="
            + oppgaveSaksNr + ", behandlingType=" + behandlingType + '}';
    }
}
