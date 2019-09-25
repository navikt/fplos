package no.nav.fplos.kafkatjenester.genereltgrensesnitt.attributt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class HeltallAttributt extends Attributt {
    private Integer verdi;

    @JsonCreator
    public HeltallAttributt(@JsonProperty("felt") String felt,
                            @JsonProperty("verdi") Integer verdi) {
        super(felt);
        this.verdi = verdi;
    }

    public Integer getVerdi() {
        return verdi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeltallAttributt that = (HeltallAttributt) o;
        return verdi.equals(that.verdi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verdi);
    }

    @Override
    public String toString() {
        return "HeltallAttributt{" +
                "felt='" + super.getFelt() + '\'' +
                ", verdi=" + verdi +
                '}';
    }
}