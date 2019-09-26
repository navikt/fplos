package no.nav.fplos.kafkatjenester.genereltgrensesnitt.attributt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

public class TimestampAttributt extends Attributt {
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime verdi;

    @JsonCreator
    public TimestampAttributt(@JsonProperty("felt") String felt,
                            @JsonProperty("verdi") LocalDateTime verdi) {
        super(felt);
        this.verdi = verdi;
    }

    public LocalDateTime getVerdi() {
        return verdi;
    }

    @Override
    public String toString() {
        return "TimestampAttributt{" +
                "felt='" + super.getFelt() + '\'' +
                ", verdi=" + verdi +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TimestampAttributt other = (TimestampAttributt) o;
        return this.verdi.equals(other.verdi);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (verdi != null ? verdi.hashCode() : 0);
        return result;
    }
}