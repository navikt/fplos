package no.nav.fplos.kafkatjenester.genereltgrensesnitt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class TimestampAttributt extends Attributt {
    private LocalDateTime verdi;

    @JsonCreator
    public TimestampAttributt(@JsonProperty("felt") String felt,
                            @JsonProperty("verdi") LocalDateTime verdi) {
        super(felt);
        this.verdi = verdi;
    }

    @Override
    public String toString() {
        return "TimestampAttributt{" +
                "felt='" + super.getFelt() + '\'' +
                "verdi=" + verdi +
                '}';
    }
}