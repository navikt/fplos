package no.nav.foreldrepenger.los.klient.fpsak.dto.aksjonspunkt;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.los.klient.fpsak.dto.kodeverk.KodeDto;

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
