package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.los.oppgave.FagsakStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FagsakDto {

    private String aktoerId;
    //private Long saksnummer;
    private String saksnummerString;
    private FagsakYtelseTypeDto sakstype;
    private FagsakStatus status;
    private LocalDate barnFodt;

    public FagsakDto() {
        // Injiseres i test
    }

    public FagsakDto(String aktoerId, String saksnummerString, FagsakYtelseTypeDto sakstype, FagsakStatus status, LocalDate barnFodt) {
        this.aktoerId = aktoerId;
        //this.saksnummer = Long.parseLong(saksnummerString);
        this.saksnummerString = saksnummerString;
        this.sakstype = sakstype;
        this.status = status;
        this.barnFodt = barnFodt;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    /*public Long getSaksnummer() {
        return saksnummer;
    }*/

    public String getSaksnummerString() {
        return saksnummerString;
    }

    public FagsakYtelseTypeDto getSakstype() {
        return sakstype;
    }

    public FagsakStatus getStatus() {
        return status;
    }

    public LocalDate getBarnFodt() {
        return barnFodt;
    }

}
