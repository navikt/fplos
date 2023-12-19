package no.nav.foreldrepenger.los.tjenester.felles.dto;

import static no.nav.foreldrepenger.los.felles.util.BrukerIdent.brukerIdent;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.los.reservasjon.Reservasjon;

public class OppgaveStatusDto {

    private final boolean erReservert;
    private LocalDateTime reservertTilTidspunkt;
    private Boolean erReservertAvInnloggetBruker;
    private String reservertAvUid;
    private String reservertAvNavn;
    private FlyttetReservasjonDto flyttetReservasjon;

    static OppgaveStatusDto reservert(Reservasjon reservasjon, String reservertAvNavn, String navnFlyttetAv) {
        var reservasjonDto = new ReservasjonDto(reservasjon, reservertAvNavn, navnFlyttetAv);
        return new OppgaveStatusDto(true, reservasjonDto, null);
    }

    static OppgaveStatusDto reservert(Reservasjon reservasjon, String reservertAvNavn, FlyttetReservasjonDto flyttetReservasjonDto) {
        var reservasjonDto = new ReservasjonDto(reservasjon, reservertAvNavn, flyttetReservasjonDto.getNavn());
        return new OppgaveStatusDto(true, reservasjonDto, flyttetReservasjonDto);
    }

    static OppgaveStatusDto ikkeReservert() {
        return new OppgaveStatusDto(false);
    }

    private OppgaveStatusDto(boolean erReservert, ReservasjonDto reservasjonDto, FlyttetReservasjonDto flyttetReservasjonDto) {
        this.erReservert = erReservert;
        this.reservertTilTidspunkt = reservasjonDto.reservertTilTidspunkt();
        this.reservertAvUid = reservasjonDto.reservertAvUid();
        this.reservertAvNavn = reservasjonDto.reservertAvNavn();
        this.erReservertAvInnloggetBruker = isErReservertAvInnloggetBruker(reservertAvUid);

        if (reservasjonDto.begrunnelse() != null || reservasjonDto.flyttetTidspunkt() != null) {
            if (flyttetReservasjonDto != null) {
                flyttetReservasjon = flyttetReservasjonDto;
            } else {
                flyttetReservasjon = new FlyttetReservasjonDto(reservasjonDto.flyttetTidspunkt(), reservasjonDto.flyttetAv(),
                    reservasjonDto.flyttetAvNavn(), reservasjonDto.begrunnelse());
            }
        }
    }

    private OppgaveStatusDto(boolean erReservert) {
        this.erReservert = erReservert;
    }

    public boolean isErReservert() {
        return erReservert;
    }

    public LocalDateTime getReservertTilTidspunkt() {
        return reservertTilTidspunkt;
    }

    public Boolean getErReservertAvInnloggetBruker() {
        return erReservertAvInnloggetBruker;
    }

    public String getReservertAvUid() {
        return reservertAvUid;
    }

    public String getReservertAvNavn() {
        return reservertAvNavn;
    }

    private static boolean isErReservertAvInnloggetBruker(String reservertAvUid) {
        return reservertAvUid != null && reservertAvUid.equalsIgnoreCase(brukerIdent());
    }

    public FlyttetReservasjonDto getFlyttetReservasjon() {
        return flyttetReservasjon;
    }
}
