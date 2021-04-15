package no.nav.foreldrepenger.los.klient.fpsak.dto.aksjonspunkt;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.los.klient.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AksjonspunktDto(KodeDto definisjon, KodeDto status, String begrunnelse, LocalDateTime fristTid) {

    //Må override siden begrunnelse er sensitivt
    @Override
    public String toString() {
        return "AksjonspunktDto{" + "definisjon=" + definisjon + ", status=" + status + ", fristTid=" + fristTid + '}';
    }
}
