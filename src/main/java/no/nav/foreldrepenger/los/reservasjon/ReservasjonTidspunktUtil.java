package no.nav.foreldrepenger.los.reservasjon;

import no.nav.foreldrepenger.los.felles.util.DateAndTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ReservasjonTidspunktUtil {

    private ReservasjonTidspunktUtil() {
    }

    public static LocalDateTime standardReservasjon() {
        return LocalDate.now().plusDays(1).with(DateAndTimeUtil.justerTilNesteUkedag).atTime(19, 0);
    }

    public static LocalDateTime utvidReservasjon(LocalDateTime eksisterende) {
        return eksisterende.plusHours(24).with(DateAndTimeUtil.justerTilNesteUkedag);
    }

    public static LocalDateTime utledReservasjonTidspunkt(LocalDate date) {
        var localDateTime = date.atTime(23, 59);
        sjekkGrenseverdier(localDateTime);
        return localDateTime;
    }

    private static void sjekkGrenseverdier(LocalDateTime tidspunkt) throws IllegalArgumentException {
        var now = LocalDateTime.now();
        if (tidspunkt.isBefore(now)) {
            throw new IllegalArgumentException("Reservasjon kan ikke avsluttes før dagens dato");
        }
        if (tidspunkt.isAfter(now.plusDays(31))) {
            throw new IllegalArgumentException(
                "Reservasjon kan ikke være lenger enn 30 dager"); //Siden vi bruker LocalDateTime med 23:59 for sjekken så justeres der til påfølgende dag
        }
    }
}
