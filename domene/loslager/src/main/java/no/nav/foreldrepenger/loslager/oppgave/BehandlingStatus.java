package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BehandlingStatus {
    AVSLUTTET("AVSLU"),
    FATTER_VEDTAK("FVED"),
    IVERKSETTER_VEDTAK("IVED"),
    OPPRETTET("OPPRE"),
    UTREDES("UTRED");

    private String kode;

    BehandlingStatus(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

    @JsonCreator
    public static BehandlingStatus fraKode(@JsonProperty("kode") String kode) {
        if (kode.equals("-")) return null;
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow();
    }

    public static List<BehandlingStatus> getEnums() {
        return Arrays.stream(values())
                .collect(Collectors.toList());
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<BehandlingStatus, String> {
        @Override
        public String convertToDatabaseColumn(BehandlingStatus attribute) {
            return Optional.ofNullable(attribute)
                    .map(BehandlingStatus::getKode)
                    .orElse(null);
        }

        @Override
        public BehandlingStatus convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData)
                    .map(BehandlingStatus::fraKode)
                    .orElse(null);
        }
    }
}
