package no.nav.fplos.kafkatjenester.genereltgrensesnitt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimestampAttributt.class, name = "typenavn"),
        @JsonSubTypes.Type(value = HeltallAttributt.class, name = "heltall"),
        @JsonSubTypes.Type(value = BooleanAttributt.class, name = "boolean")
})
public abstract class Attributt {
    private String felt;

    @JsonCreator
    public Attributt(@JsonProperty("felt") String felt) {
        this.felt = felt;
    }

    @Override
    public String toString() {
        return "Attributt{" +
                "felt='" + felt + '\'' +
                '}';
    }
}
