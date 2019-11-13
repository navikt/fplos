package no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.fplos.foreldrepengerbehandling.dto.kodeverk.KodeDto;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AksjonspunktDto {
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
