package no.nav.fplos.kafkatjenester.genereltgrensesnitt.attributt;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BooleanAttributt extends Attributt {
    private boolean verdi;

    @JsonCreator
    public BooleanAttributt(@JsonProperty("felt") String felt,
                            @JsonProperty("verdi") boolean verdi) {
        super(felt);
        this.verdi = verdi;
    }

    public boolean isVerdi() {
        return verdi;
    }

    @Override
    public String toString() {
        return "BooleanAttributt{" +
                "felt='" + super.getFelt() + '\'' +
                ", verdi=" + verdi +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(verdi);
    }
}
