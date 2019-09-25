package no.nav.fplos.kafkatjenester.genereltgrensesnitt.attributt;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimestampAttributt.class, name = "timestamp"),
        @JsonSubTypes.Type(value = HeltallAttributt.class, name = "heltall"),
        @JsonSubTypes.Type(value = BooleanAttributt.class, name = "boolean")
})
public abstract class Attributt {
    private String felt;

    Attributt(String felt) {
        this.felt = felt;
    }

    public String getFelt() {
        return felt;
    }

    @Override
    public String toString() {
        return "Attributt{" +
                "felt='" + felt + '\'' +
                '}';
    }
}
