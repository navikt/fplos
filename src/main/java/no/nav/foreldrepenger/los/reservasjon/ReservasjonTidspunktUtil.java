package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;

public final class ReservasjonTidspunktUtil {

    public static final TemporalAdjuster JUSTER_TIL_GYLDIG_TIDSPUNKT = temporal -> {
        var dateTime = LocalDateTime.from(temporal).withHour(23).withMinute(59);
        return switch (dateTime.getDayOfWeek()) {
            case SATURDAY -> dateTime.plusDays(2);
            case SUNDAY -> dateTime.plusDays(1);
            default -> dateTime;
        };
    };

    private ReservasjonTidspunktUtil() {
    }

    public static LocalDateTime standardReservasjon() {
        return LocalDateTime.now().plusDays(1).with(JUSTER_TIL_GYLDIG_TIDSPUNKT);
    }

    public static void validerReservasjonTil(LocalDate reserverTil) throws IllegalArgumentException {
        var iDag = LocalDate.now();
        if (reserverTil.isBefore(iDag)) {
            throw new IllegalArgumentException("Reservasjon kan ikke avsluttes før dagens dato");
        }
        if (reserverTil.isAfter(iDag.plusDays(30))) {
            throw new IllegalArgumentException("Reservasjon kan ikke være lenger enn 30 dager");
        }
    }
}
