package no.nav.foreldrepenger.loslager.oppgave;

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BehandlingType implements Kodeverdi {
    FØRSTEGANGSSØKNAD ("BT-002", "Førstegangsbehandling"),
    KLAGE("BT-003", "Klage"),
    REVURDERING("BT-004", "Revurdering"),
    SØKNAD("BT-005", "Søknad"),
    INNSYN("BT-006", "Innsyn"),
    TILBAKEBETALING ("BT-007", "Tilbakebetaling"),
    ANKE("BT-008", "Anke"),
    TILBAKEBETALING_REVURDERING ("BT-009", "Tilbakebet-rev");

    private String kode;
    private final String navn;
    public static final String kodeverk = "BEHANDLING_TYPE";

    BehandlingType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() { return kode; }

    public String getKodeverk() {
        return kodeverk;
    }

    @JsonCreator
    public static BehandlingType fraKode(@JsonProperty("kode") String kode) {
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ukjent BehandlingType: " + kode));
    }


    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<BehandlingType, String> {
        @Override
        public String convertToDatabaseColumn(BehandlingType attribute) {
            return Optional.ofNullable(attribute)
                    .map(BehandlingType::getKode)
                    .orElse(null);
        }

        @Override
        public BehandlingType convertToEntityAttribute(String dbData) {
            return Optional.ofNullable(dbData)
                    .map(BehandlingType::fraKode)
                    .orElse(null);
        }
    }
}
