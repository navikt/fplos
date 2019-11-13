package no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.fplos.foreldrepengerbehandling.dto.kodeverk.KodeDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AksjonspunktDto {
    private static final String AKTIV_STATUS = "OPPR";
    private static final String AVBRUTT_STATUS = "AVBR";
    private static final String MANUELT_SATT_PÅ_VENT_KODE = "7001";
    private static final String PÅ_VENT_KODEGRUPPE_STARTS_WITH = "7";
    private static final String TIL_BESLUTTER_KODE = "5016";
    private static final List<String> REGISTRER_PAPIRSØKNAD_KODE = asList("5012", "5040", "5057", "5096");
    public static final String AUTOMATISK_MARKERING_SOM_UTENLANDSSAK_KODE = "5068";

    private KodeDto definisjon;
    private KodeDto status;
    private String begrunnelse;
    private LocalDateTime fristTid;

    public AksjonspunktDto() {
    }

    public KodeDto getDefinisjon() {
        return definisjon;
    }

    public KodeDto getStatus() {
        return status;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public LocalDateTime getFristTid() {
        return fristTid;
    }

    public boolean erPåVent() {
        return definisjon.getKode().startsWith(PÅ_VENT_KODEGRUPPE_STARTS_WITH) && erAktiv();
    }

    public boolean erManueltPåVent() {
        return MANUELT_SATT_PÅ_VENT_KODE.equals(definisjon.getKode()) && erAktiv();
    }

    public boolean erAktiv() {
        return AKTIV_STATUS.equals(status.getKode());
    }

    public boolean erAvbrutt() {
        return AVBRUTT_STATUS.equals(definisjon.getKode());
    }

    public boolean tilBeslutter() {
        return TIL_BESLUTTER_KODE.equals(definisjon.getKode()) && erAktiv();
    }

    public boolean erRegistrerPapirSøknad() {
        return REGISTRER_PAPIRSØKNAD_KODE.contains(definisjon.getKode()) && erAktiv();
    }

    public boolean erAutomatiskMarkertSomUtenlandssak() {
        return AUTOMATISK_MARKERING_SOM_UTENLANDSSAK_KODE.equals(definisjon.getKode()) && !erAvbrutt();
    }

    public static AksjonspunktDto.Builder builder() {
        return new AksjonspunktDto.Builder();
    }

    public static class Builder {
        private AksjonspunktDto aksjonspunktDto;

        public Builder(){
            aksjonspunktDto = new AksjonspunktDto();
        }

        public Builder medDefinisjon(String definisjonKode){
            aksjonspunktDto.definisjon = new KodeDto("", definisjonKode,"");
            return this;
        }

        public Builder medStatus(String statusKode){
            aksjonspunktDto.status = new KodeDto("", statusKode, "");
            return this;
        }

        public Builder medBegrunnelse(String begrunnelse){
            aksjonspunktDto.begrunnelse = begrunnelse;
            return this;
        }

        public Builder medFristTid(LocalDateTime fristTid){
            aksjonspunktDto.fristTid = fristTid;
            return this;
        }

        public AksjonspunktDto build() {
            return aksjonspunktDto;
        }
    }

    @Override
    public String toString() {
        return "AksjonspunktDto{" +
                "definisjon=" + definisjon +
                ", status=" + status +
                ", begrunnelse='" + begrunnelse + '\'' +
                ", fristTid=" + fristTid +
                '}';
    }
}
