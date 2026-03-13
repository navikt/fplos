package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;

public final class ReservasjonTidspunktUtil {

    public static final TemporalAdjuster JUSTER_TIL_GYLDIG_TIDSPUNKT = temporal -> {
        var justertDateTime = LocalDateTime.from(temporal).withHour(23).withMinute(59).withSecond(59);
        return switch (justertDateTime.getDayOfWeek()) {
            case SATURDAY -> justertDateTime.plusDays(2);
            case SUNDAY -> justertDateTime.plusDays(1);
            default -> justertDateTime;
        };
    };

    private ReservasjonTidspunktUtil() {
    }

    public static LocalDateTime standardReservasjon() {
        return LocalDateTime.now().plusDays(1).with(JUSTER_TIL_GYLDIG_TIDSPUNKT);
    }

    public static void validerReservasjonsdato(LocalDate reserverTil) throws IllegalArgumentException {
        var iDag = LocalDate.now();
        if (reserverTil.isBefore(iDag)) {
            throw new IllegalArgumentException("Reservasjon kan ikke avsluttes før dagens dato");
        }
        if (reserverTil.isAfter(iDag.plusDays(30))) {
            throw new IllegalArgumentException("Reservasjon kan ikke være lenger enn 30 dager");
        }
    }
}
