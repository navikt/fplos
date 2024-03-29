package no.nav.foreldrepenger.los.tjenester.reservasjon;

import java.time.LocalDate;
import java.time.LocalDateTime;

final class ReservasjonTidspunktUtil {

    private ReservasjonTidspunktUtil() {
    }

    static LocalDateTime utledReservasjonTidspunkt(LocalDate date) {
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
