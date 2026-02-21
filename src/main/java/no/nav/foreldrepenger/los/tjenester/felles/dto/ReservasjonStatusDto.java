package no.nav.foreldrepenger.los.tjenester.felles.dto;

import static no.nav.foreldrepenger.los.felles.util.BrukerIdent.brukerIdent;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

public record ReservasjonStatusDto(@NotNull boolean erReservert, LocalDateTime reservertTilTidspunkt, Boolean erReservertAvInnloggetBruker,
                                   String reservertAvIdent, String reservertAvNavn, FlyttetReservasjonDto flyttetReservasjon) {

    static ReservasjonStatusDto reservert(Reservasjon reservasjon, String reservertAvNavn, String navnFlyttetAv) {
        var reservasjonDto = new ReservasjonDto(reservasjon, reservertAvNavn, navnFlyttetAv);
        return new ReservasjonStatusDto(true, reservasjonDto, null);
    }

    static ReservasjonStatusDto reservert(Reservasjon reservasjon, String reservertAvNavn, FlyttetReservasjonDto flyttetReservasjonDto) {
        var reservasjonDto = new ReservasjonDto(reservasjon, reservertAvNavn, flyttetReservasjonDto.navn());
        return new ReservasjonStatusDto(true, reservasjonDto, flyttetReservasjonDto);
    }

    static ReservasjonStatusDto ikkeReservert() {
        return new ReservasjonStatusDto(false);
    }

    private ReservasjonStatusDto(boolean erReservert, ReservasjonDto reservasjonDto, FlyttetReservasjonDto flyttetReservasjonDto) {
        this(erReservert, reservasjonDto.reservertTilTidspunkt(), isErReservertAvInnloggetBruker(reservasjonDto.reservertAvIdent()),
            reservasjonDto.reservertAvIdent(), reservasjonDto.reservertAvNavn(), utledFlyttetReservasjonDto(reservasjonDto, flyttetReservasjonDto));
    }

    private ReservasjonStatusDto(boolean erReservert) {
        this(erReservert, null, null, null, null, null);
    }

    private static boolean isErReservertAvInnloggetBruker(String reservertAvIdent) {
        return reservertAvIdent != null && reservertAvIdent.equalsIgnoreCase(brukerIdent());
    }

    private static FlyttetReservasjonDto utledFlyttetReservasjonDto(ReservasjonDto reservasjonDto, FlyttetReservasjonDto flyttetReservasjonDto) {
        if (reservasjonDto.begrunnelse() == null && reservasjonDto.flyttetTidspunkt() == null) {
            return null;
        }
        return flyttetReservasjonDto != null ? flyttetReservasjonDto : new FlyttetReservasjonDto(reservasjonDto.flyttetTidspunkt(),
            reservasjonDto.flyttetAvIdent(), reservasjonDto.flyttetAvNavn(), reservasjonDto.begrunnelse());
    }

}
