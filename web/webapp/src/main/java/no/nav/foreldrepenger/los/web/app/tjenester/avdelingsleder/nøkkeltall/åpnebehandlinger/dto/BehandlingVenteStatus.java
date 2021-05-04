package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import no.nav.foreldrepenger.los.felles.Kodeverdi;

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
