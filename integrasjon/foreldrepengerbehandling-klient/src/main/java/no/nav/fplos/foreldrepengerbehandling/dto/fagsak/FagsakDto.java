package no.nav.fplos.foreldrepengerbehandling.dto.fagsak;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FagsakDto {

    private String aktoerId;
    private Long saksnummer;
    private FagsakYtelseTypeDto sakstype;
    private FagsakStatus status;
    private LocalDate barnFodt;

    public FagsakDto() {
        // Injiseres i test
    }

    public FagsakDto(String aktoerId, Long saksnummer, FagsakYtelseTypeDto sakstype, FagsakStatus status, LocalDate barnFodt) {
        this.aktoerId = aktoerId;
        this.saksnummer = saksnummer;
        this.sakstype = sakstype;
        this.status = status;
        this.barnFodt = barnFodt;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public Long getSaksnummer() {
        return saksnummer;
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
