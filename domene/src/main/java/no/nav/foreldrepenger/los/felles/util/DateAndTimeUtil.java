package no.nav.foreldrepenger.los.felles.util;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

public final class DateAndTimeUtil {

    public static final TemporalAdjuster justerTilNesteUkedag = TemporalAdjusters.ofDateAdjuster(d -> {
        var day = d.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
                ? d.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                : d;
    });

    private DateAndTimeUtil() {
    }
}
