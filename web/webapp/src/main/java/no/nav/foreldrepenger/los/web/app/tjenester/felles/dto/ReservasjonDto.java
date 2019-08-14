package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;

import java.time.LocalDateTime;

public class ReservasjonDto {

    private LocalDateTime reservertTilTidspunkt;
    private String reservertAvUid;
    private String reservertAvNavn;  // Settes når oppgave er reservert av annen saksbehandler
    private LocalDateTime flyttetTidspunkt;
    private String flyttetAv;
    private String flyttetAvNavn;
    private String begrunnelse;

    public ReservasjonDto(Reservasjon reservasjon, String reservertAvNavn, String navnFlyttetAv) {
        this.reservertTilTidspunkt = reservasjon.getReservertTil();
        this.reservertAvUid = reservasjon.getReservertAv();
        this.reservertAvNavn = reservertAvNavn;
        this.flyttetTidspunkt = reservasjon.getFlyttetTidspunkt();
        this.flyttetAv = reservasjon.getFlyttetAv();
        this.flyttetAvNavn = navnFlyttetAv;
        this.begrunnelse = reservasjon.getBegrunnelse();
    }

    public LocalDateTime getReservertTilTidspunkt() {
        return reservertTilTidspunkt;
    }

    public String getReservertAvUid() {
        return reservertAvUid;
    }

    public String getReservertAvNavn() {
        return reservertAvNavn;
    }

    public LocalDateTime getFlyttetTidspunkt() {
        return flyttetTidspunkt;
    }

    public String getFlyttetAv() {
        return flyttetAv;
    }

    public String getFlyttetAvNavn() {
        return flyttetAvNavn;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }
}
