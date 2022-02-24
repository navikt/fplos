package no.nav.foreldrepenger.los.klient.fpsak.dto.aksjonspunkt;

import java.time.LocalDateTime;

public record AksjonspunktDto(String definisjon, String status, String begrunnelse, LocalDateTime fristTid) {

    //Må override siden begrunnelse er sensitivt
    @Override
    public String toString() {
        return "AksjonspunktDto{" + "definisjon=" + definisjon + ", status=" + status + ", fristTid=" + fristTid + '}';
    }
}
