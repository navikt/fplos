package no.nav.fplos.kafkatjenester.genereltgrensesnitt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HeltallAttributt extends Attributt {
    private Integer verdi;

    @JsonCreator
    public HeltallAttributt(@JsonProperty("felt") String felt,
                            @JsonProperty("verdi") Integer verdi) {
        super(felt);
        this.verdi = verdi;
    }

    @Override
    public String toString() {
        return "HeltallAttributt{" +
                "felt='" + super.getFelt() + '\'' +
                ", verdi=" + verdi +
                '}';
    }
}