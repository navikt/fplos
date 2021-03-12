package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.los.oppgave.FagsakStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FagsakDto {

    private String aktoerId;
    private String saksnummer;
    private FagsakYtelseTypeDto fagsakYtelseType;
    private FagsakStatus status;
    private LocalDate barnFodt;

    public FagsakDto() {
        // Injiseres i test
    }

    public FagsakDto(String aktoerId, String saksnummer, FagsakYtelseTypeDto fagsakYtelseType, FagsakStatus status, LocalDate barnFodt) {
        this.aktoerId = aktoerId;
        this.saksnummer = saksnummer;
        this.fagsakYtelseType = fagsakYtelseType;
        this.status = status;
        this.barnFodt = barnFodt;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public FagsakYtelseTypeDto getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public FagsakStatus getStatus() {
        return status;
    }

    public LocalDate getBarnFodt() {
        return barnFodt;
    }

}
