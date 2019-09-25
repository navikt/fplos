package no.nav.fplos.kafkatjenester.genereltgrensesnitt;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BooleanAttributt extends Attributt {
    private boolean verdi;

    @JsonCreator
    public BooleanAttributt(@JsonProperty("felt") String felt,
                            @JsonProperty("verdi") boolean verdi) {
        super(felt);
        this.verdi = verdi;
    }

    @Override
    public String toString() {
        return "BooleanAttributt{" +
                "felt=" + super.toString() +
                "verdi=" + verdi +
                '}';
    }
}
