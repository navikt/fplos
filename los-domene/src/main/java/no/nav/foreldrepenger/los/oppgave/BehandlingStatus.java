package no.nav.foreldrepenger.los.oppgave;

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BehandlingStatus {
    AVSLUTTET("AVSLU"),
    FATTER_VEDTAK("FVED"),
    IVERKSETTER_VEDTAK("IVED"),
    OPPRETTET("OPPRE"),
    UTREDES("UTRED");

    @JsonValue
    private String kode;

    BehandlingStatus(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
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
                    .map(KodeverdiConverter::fraKode)
                    .orElse(null);
        }

        private static BehandlingStatus fraKode(String kode) {
            return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ukjent BehandlingStatus: " + kode));
        }
    }
}
