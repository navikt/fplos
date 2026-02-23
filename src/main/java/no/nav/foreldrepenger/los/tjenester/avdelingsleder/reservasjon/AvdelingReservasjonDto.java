package no.nav.foreldrepenger.los.tjenester.avdelingsleder.reservasjon;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

public record AvdelingReservasjonDto(LocalDateTime reservertTilTidspunkt, String reservertAvIdent, String reservertAvNavn,
                                     Long oppgaveId, String oppgaveSaksNr, BehandlingType behandlingType) {

    public AvdelingReservasjonDto(Reservasjon reservasjon, String reservertAvNavn) {
        this(reservasjon.getReservertTil(), reservasjon.getReservertAv(), reservertAvNavn,
            reservasjon.getOppgave().getId(), reservasjon.getOppgave().getSaksnummer().getVerdi(),
            reservasjon.getOppgave().getBehandlingType());
    }

    @Override
    public String toString() {
        return "ReservasjonDto{" + "reservertTilTidspunkt=" + reservertTilTidspunkt + ", reservertAvIdent='" + reservertAvIdent + '\''
            + ", oppgaveId=" + oppgaveId + ", oppgaveSaksNr=" + oppgaveSaksNr + ", behandlingType=" + behandlingType + '}';
    }
}
