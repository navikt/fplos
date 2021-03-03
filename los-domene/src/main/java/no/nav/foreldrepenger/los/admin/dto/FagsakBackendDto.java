package no.nav.foreldrepenger.los.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FagsakBackendDto {
    //private Long saksnummer;
    private String saksnummerString;
    private FagsakYtelseType sakstype;
    private String aktoerId;



    public FagsakBackendDto() {
    }

    /* public Long getSaksnummer() {
        return saksnummer;
    }

    public void setSaksnummer(Long saksnummer) {
        this.saksnummer = saksnummer;
    }*/

    public String getSaksnummerString() {
        return saksnummerString;
    }

    public void setSaksnummerString(String saksnummerString) {
        this.saksnummerString = saksnummerString;
    }

    public FagsakYtelseType getSakstype() {
        return sakstype;
    }

    public void setSakstype(FagsakYtelseType sakstype) {
        this.sakstype = sakstype;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(String aktoerId) {
        this.aktoerId = aktoerId;
    }
}
