package no.nav.fplos.oppgave;

import java.util.List;

public class SaksbehandlerinformasjonDto {
    private String saksbehandlerIdent;
    private String navn;
    private List<String> avdelinger;

    public SaksbehandlerinformasjonDto(String saksbehandlerIdent, String navn, List<String> avdelinger) {
        this.saksbehandlerIdent = saksbehandlerIdent;
        this.navn = navn;
        this.avdelinger = avdelinger;
    }

    public String getSaksbehandlerIdent() {
        return saksbehandlerIdent;
    }

    public String getNavn() {
        return navn;
    }

    public List<String> getAvdelinger() {
        return avdelinger;
    }

    @Override
    public String toString() {
        return "SaksbehandlerinformasjonDto{" +
                ", saksbehandlerIdent='" + saksbehandlerIdent + '\'' +
                ", navn='" + navn + '\'' +
                ", avdelinger=" + avdelinger +
                '}';
    }
}
