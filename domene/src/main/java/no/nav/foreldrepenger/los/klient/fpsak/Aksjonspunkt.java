package no.nav.foreldrepenger.los.klient.fpsak;

import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public class Aksjonspunkt {
    public static final String STATUSKODE_AKTIV = "OPPR";
    public static final String STATUSKODE_AVBRUTT = "AVBR";
    public static final String STATUSKODE_UTFØRT = "UTFO";

    public static final String MANUELT_SATT_PÅ_VENT_KODE = "7001";
    public static final String PÅ_VENT_KODEGRUPPE_STARTS_WITH = "7";
    public static final String TIL_BESLUTTER_KODE = "5016";
    protected static final List<String> REGISTRER_PAPIRSØKNAD_KODE = asList("5012", "5040", "5057", "5096");
    protected static final List<String> VURDER_FORMKRAV_GRUPPE = asList("5082", "5083");

    public static final String AUTOMATISK_MARKERING_SOM_UTLAND = "5068";

    private String definisjonKode;
    private String statusKode;
    private String begrunnelse;
    private LocalDateTime fristTid;

    public Aksjonspunkt() {
        // Jackson
    }

    public Aksjonspunkt(String definisjonKode, String statusKode, String begrunnelse) {
        this(definisjonKode, statusKode, begrunnelse, null);
    }

    public Aksjonspunkt(String definisjonKode, String statusKode, String begrunnelse, LocalDateTime fristTid) {
        this.definisjonKode = definisjonKode;
        this.statusKode = statusKode;
        this.begrunnelse = begrunnelse;
        this.fristTid = fristTid;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public LocalDateTime getFristTid() {
        return fristTid;
    }

    public String getDefinisjonKode() {
        return definisjonKode;
    }

    public String getStatusKode() {
        return statusKode;
    }

    public boolean erPåVent() {
        return definisjonKode != null && definisjonKode.startsWith(PÅ_VENT_KODEGRUPPE_STARTS_WITH) && erAktiv();
    }

    public boolean erManueltPåVent() {
        return MANUELT_SATT_PÅ_VENT_KODE.equals(definisjonKode) && erAktiv();
    }

    public boolean erAktiv() {
        return STATUSKODE_AKTIV.equals(statusKode);
    }

    public boolean erAvbrutt() {
        return STATUSKODE_AVBRUTT.equals(definisjonKode);
    }

    public boolean erTilBeslutter() {
        return TIL_BESLUTTER_KODE.equals(definisjonKode) && erAktiv();
    }

    public boolean erRegistrerPapirSøknad() {
        return REGISTRER_PAPIRSØKNAD_KODE.contains(definisjonKode) && erAktiv();
    }

    public boolean skalVurdereInnhentingAvSED() {
        return erAktiv() && AUTOMATISK_MARKERING_SOM_UTLAND.equals(definisjonKode);
    }

    public boolean erVurderFormkrav() {
        return erAktiv() && VURDER_FORMKRAV_GRUPPE.contains(definisjonKode);
    }

    public static Aksjonspunkt aksjonspunktFra(LosBehandlingDto.LosAksjonspunktDto aksjonspunktDto) {
        return Aksjonspunkt.builder()
            .medDefinisjon(aksjonspunktDto.definisjon())
            .medStatus(mapAksjonspunktstatus(aksjonspunktDto.status()))
            .medBegrunnelse(aksjonspunktDto.begrunnelse())
            .medFristTid(aksjonspunktDto.fristTid())
            .build();
    }

    private static String mapAksjonspunktstatus(Aksjonspunktstatus status) {
        return switch (status) {
            case OPPRETTET -> STATUSKODE_AKTIV;
            case AVBRUTT -> STATUSKODE_AVBRUTT;
            case UTFØRT -> STATUSKODE_UTFØRT;
        };
    }

    public static Aksjonspunkt.Builder builder() {
        return new Aksjonspunkt.Builder();
    }

    public static class Builder {
        private final Aksjonspunkt aksjonspunkt;

        public Builder() {
            aksjonspunkt = new Aksjonspunkt();
        }

        public Aksjonspunkt.Builder medDefinisjon(String definisjonKode) {
            aksjonspunkt.definisjonKode = definisjonKode;
            return this;
        }

        public Aksjonspunkt.Builder medStatus(String statusKode) {
            aksjonspunkt.statusKode = statusKode;
            return this;
        }

        public Aksjonspunkt.Builder medBegrunnelse(String begrunnelse) {
            aksjonspunkt.begrunnelse = begrunnelse;
            return this;
        }

        public Aksjonspunkt.Builder medFristTid(LocalDateTime fristTid) {
            aksjonspunkt.fristTid = fristTid;
            return this;
        }

        public Aksjonspunkt build() {
            return aksjonspunkt;
        }
    }

}
