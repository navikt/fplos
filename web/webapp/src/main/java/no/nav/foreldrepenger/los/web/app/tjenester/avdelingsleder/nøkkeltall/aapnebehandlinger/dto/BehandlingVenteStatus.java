package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.foreldrepenger.loslager.oppgave.Kodeverdi;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BehandlingVenteStatus implements Kodeverdi {
    PÅ_VENT("PÅ_VENT", "På vent"),
    IKKE_PÅ_VENT("IKKE_PÅ_VENT", "Ikke på vent");

    private String kode;
    private final String navn;
    public static final String kodeverk = "BEHANDLING_VENTE_STATUS";

    BehandlingVenteStatus(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static BehandlingVenteStatus fraKode(@JsonProperty("kode") String kode) {
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ukjent BehandlingVenteStatus: " + kode));
    }

    @Override
    public String getNavn() {
        return navn;
    }

    @Override
    public String getKode() {
        return kode;
    }

    @Override
    public String getKodeverk() {
        return kodeverk;
    }
}
