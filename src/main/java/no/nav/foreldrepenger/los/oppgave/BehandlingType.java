package no.nav.foreldrepenger.los.oppgave;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.EnumeratedValue;
import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum BehandlingType implements Kodeverdi {
    FØRSTEGANGSSØKNAD("BT-002", "Førstegangsbehandling"),
    KLAGE("BT-003", "Klage"),
    REVURDERING("BT-004", "Revurdering"),
    INNSYN("BT-006", "Innsyn"),
    TILBAKEBETALING("BT-007", "Tilbakebetaling"),
    ANKE("BT-008", "Anke"),
    TILBAKEBETALING_REVURDERING("BT-009", "Tilbakebetaling revurdering");

    @JsonValue
    @EnumeratedValue
    private final String kode;
    @JsonIgnore
    private final String navn;

    BehandlingType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }

    public boolean gjelderTilbakebetaling() {
        return this == TILBAKEBETALING || this == TILBAKEBETALING_REVURDERING;
    }

    public static BehandlingType fraKode(String kode) {
        if (kode == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(v -> v.kode.equals(kode))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ukjent BehandlingType: " + kode));
    }


}
