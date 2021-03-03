package no.nav.foreldrepenger.los.klient.fpsak;

import no.nav.foreldrepenger.los.klient.fpsak.dto.aksjonspunkt.AksjonspunktDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

public class Aksjonspunkt {
    public static final String STATUSKODE_AKTIV = "OPPR";
    public static final String STATUSKODE_AVBRUTT = "AVBR";

    public static final String MANUELT_SATT_PÅ_VENT_KODE = "7001";
    public static final String PÅ_VENT_KODEGRUPPE_STARTS_WITH = "7";
    public static final String TIL_BESLUTTER_KODE = "5016";
    public static final List<String> REGISTRER_PAPIRSØKNAD_KODE = asList("5012", "5040", "5057", "5096");
    //public static final List<String> SELVSTENDIG_FRILANSER_GRUPPE = asList("5038", "5039", "5042", "7072");
    public static final List<String> SELVSTENDIG_FRILANSER_GRUPPE = asList("5038", "5049");

    public static final String VURDER_FARESIGNALER = "5095";
    public static final String AUTOMATISK_MARKERING_SOM_UTLAND = "5068";
    public static final String MANUELL_MARKERING_SOM_UTLAND = "6068";
    public static final String EØS_BOSATT_NORGE = "EØS_BOSATT_NORGE";
    public static final String BOSATT_UTLAND = "BOSATT_UTLAND";

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
        return MANUELL_MARKERING_SOM_UTLAND.equals(definisjonKode)
                && (EØS_BOSATT_NORGE.equals(begrunnelse) || BOSATT_UTLAND.equals(begrunnelse));
    }

    public boolean erUtenlandssak() {
        return erAutomatiskMarkertSomUtenlandssak() || erManueltMarkertSomUtenlandssak();
    }

    public boolean erVurderFaresignaler() {
        return VURDER_FARESIGNALER.equals(definisjonKode) && erAktiv();
    }
//    public boolean erSelvstendigEllerFrilanser() {
//        return SELVSTENDIG_FRILANSER_GRUPPE.contains(definisjonKode) && erAktiv();

//    }

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
