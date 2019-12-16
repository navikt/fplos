package no.nav.fplos.foreldrepengerbehandling;

import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

public class Aksjonspunkt {
    private static final String STATUSKODE_AKTIV = "OPPR";
    private static final String STATUSKODE_AVBRUTT = "AVBR";

    private static final String MANUELT_SATT_PÅ_VENT_KODE = "7001";
    private static final String PÅ_VENT_KODEGRUPPE_STARTS_WITH = "7";
    private static final String TIL_BESLUTTER_KODE = "5016";
    private static final List<String> REGISTRER_PAPIRSØKNAD_KODE = asList("5012", "5040", "5057", "5096");
    private static final List<String> SELVSTENDIG_FRILANSER_GRUPPE = asList("5038", "5039", "5042", "7072");

    private static final String AUTOMATISK_MARKERING_SOM_UTLAND = "5068";
    private static final String MANUELL_MARKERING_SOM_UTLAND = "6068";
    private static final String EØS_BOSATT_NORGE = "EØS_BOSATT_NORGE";
    private static final String BOSATT_UTLAND = "BOSATT_UTLAND";

    private String definisjonKode;
    private String statusKode;
    private String begrunnelse;
    private LocalDateTime fristTid;

    public Aksjonspunkt() {
    }

    public String getDefinisjonKode() {
        return definisjonKode;
    }

    public String getStatusKode() {
        return statusKode;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public LocalDateTime getFristTid() {
        return fristTid;
    }

    public boolean erPåVent() {
        return definisjonKode.startsWith(PÅ_VENT_KODEGRUPPE_STARTS_WITH) && erAktiv();
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

    public boolean tilBeslutter() {
        return TIL_BESLUTTER_KODE.equals(definisjonKode) && erAktiv();
    }

    public boolean erRegistrerPapirSøknad() {
        return REGISTRER_PAPIRSØKNAD_KODE.contains(definisjonKode) && erAktiv();
    }

    private boolean erAutomatiskMarkertSomUtenlandssak() {
        return AUTOMATISK_MARKERING_SOM_UTLAND.equals(definisjonKode) && !erAvbrutt();
    }

    private boolean erManueltMarkertSomUtenlandssak() {
        return definisjonKode.equals(MANUELL_MARKERING_SOM_UTLAND)
                && (begrunnelse.equals(EØS_BOSATT_NORGE) || begrunnelse.equals(BOSATT_UTLAND));
    }

    public boolean erUtenlandssak() {
        return erAutomatiskMarkertSomUtenlandssak() || erManueltMarkertSomUtenlandssak();
    }

    public boolean erSelvstendigEllerFrilanser() {
        return SELVSTENDIG_FRILANSER_GRUPPE.contains(definisjonKode) && erAktiv();
    }

    public static Aksjonspunkt aksjonspunktFra(AksjonspunktDto aksjonspunktDto) {
        return Aksjonspunkt.builder()
                .medDefinisjon(aksjonspunktDto.getDefinisjon().getKode())
                .medStatus(aksjonspunktDto.getStatus().getKode())
                .medBegrunnelse(aksjonspunktDto.getBegrunnelse())
                .medFristTid(aksjonspunktDto.getFristTid())
                .build();
    }

    public static Aksjonspunkt.Builder builder() {
        return new Aksjonspunkt.Builder();
    }

    public static class Builder {
        private Aksjonspunkt aksjonspunkt;

        public Builder(){
            aksjonspunkt = new Aksjonspunkt();
        }

        public Aksjonspunkt.Builder medDefinisjon(String definisjonKode){
            aksjonspunkt.definisjonKode = definisjonKode;
            return this;
        }

        public Aksjonspunkt.Builder medStatus(String statusKode){
            aksjonspunkt.statusKode = statusKode;
            return this;
        }

        public Aksjonspunkt.Builder medBegrunnelse(String begrunnelse){
            aksjonspunkt.begrunnelse = begrunnelse;
            return this;
        }

        public Aksjonspunkt.Builder medFristTid(LocalDateTime fristTid){
            aksjonspunkt.fristTid = fristTid;
            return this;
        }

        public Aksjonspunkt build() {
            return aksjonspunkt;
        }
    }

}
