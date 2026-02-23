package no.nav.foreldrepenger.los.tjenester.felles.dto;

import static no.nav.foreldrepenger.los.felles.util.BrukerIdent.brukerIdent;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

public record ReservasjonStatusDto(@NotNull boolean erReservert, LocalDateTime reservertTilTidspunkt, Boolean erReservertAvInnloggetBruker,
                                   String reservertAvIdent, String reservertAvNavn, FlyttetReservasjonDto flyttetReservasjon) {

    static ReservasjonStatusDto reservert(Reservasjon reservasjon, String reservertAvNavn, String navnFlyttetAv) {
        var flyttet = utledFlyttetReservasjonDto(reservasjon, null, navnFlyttetAv);
        return new ReservasjonStatusDto(true, reservasjon.getReservertTil(),
            isErReservertAvInnloggetBruker(reservasjon.getReservertAv()), reservasjon.getReservertAv(), reservertAvNavn, flyttet);
    }

    static ReservasjonStatusDto reservert(Reservasjon reservasjon, String reservertAvNavn, FlyttetReservasjonDto flyttetReservasjonDto) {
        var flyttet = utledFlyttetReservasjonDto(reservasjon, flyttetReservasjonDto, null);
        return new ReservasjonStatusDto(true, reservasjon.getReservertTil(),
            isErReservertAvInnloggetBruker(reservasjon.getReservertAv()), reservasjon.getReservertAv(), reservertAvNavn, flyttet);
    }

    static ReservasjonStatusDto ikkeReservert() {
        return new ReservasjonStatusDto(false);
    }

    private ReservasjonStatusDto(boolean erReservert) {
        this(erReservert, null, null, null, null, null);
    }

    private static boolean isErReservertAvInnloggetBruker(String reservertAvIdent) {
        return reservertAvIdent != null && reservertAvIdent.equalsIgnoreCase(brukerIdent());
    }

    private static FlyttetReservasjonDto utledFlyttetReservasjonDto(Reservasjon reservasjon, FlyttetReservasjonDto flyttetReservasjonDto, String navnFlyttetAv) {
        if (reservasjon.getBegrunnelse() == null && reservasjon.getFlyttetTidspunkt() == null) {
            return null;
        }
        return flyttetReservasjonDto != null ? flyttetReservasjonDto : new FlyttetReservasjonDto(reservasjon.getFlyttetTidspunkt(),
            reservasjon.getFlyttetAv(), navnFlyttetAv, reservasjon.getBegrunnelse());
    }

}
