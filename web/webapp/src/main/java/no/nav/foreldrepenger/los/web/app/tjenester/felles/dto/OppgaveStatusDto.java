package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.time.LocalDateTime;

import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

public class OppgaveStatusDto {

    private boolean erReservert;
    private LocalDateTime reservertTilTidspunkt;
    private Boolean erReservertAvInnloggetBruker;
    private String reservertAvUid;
    private String reservertAvNavn;
    private FlyttetReservasjonDto flyttetReservasjon;

    static OppgaveStatusDto reservert(Reservasjon reservasjon, String reservertAvNavn, String navnFlyttetAv) {
        ReservasjonDto reservasjonDto = new ReservasjonDto(reservasjon, reservertAvNavn, navnFlyttetAv);
        return new OppgaveStatusDto(true, reservasjonDto);
    }

    static OppgaveStatusDto ikkeReservert() {
        return new OppgaveStatusDto(false);
    }

    private OppgaveStatusDto(boolean erReservert, ReservasjonDto reservasjonDto) {
        this.erReservert = erReservert;
        this.reservertTilTidspunkt = reservasjonDto.getReservertTilTidspunkt();
        this.reservertAvUid = reservasjonDto.getReservertAvUid();
        this.reservertAvNavn = reservasjonDto.getReservertAvNavn();
        this.erReservertAvInnloggetBruker = isErReservertAvInnloggetBruker(reservertAvUid);

        if (reservasjonDto.getFlyttetTidspunkt() != null) {
            flyttetReservasjon = new FlyttetReservasjonDto(
                    reservasjonDto.getFlyttetTidspunkt(),
                    reservasjonDto.getFlyttetAv(),
                    reservasjonDto.getFlyttetAvNavn(),
                    reservasjonDto.getBegrunnelse());
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
        return reservertAvUid != null && reservertAvUid.equalsIgnoreCase(SubjectHandler.getSubjectHandler().getUid());
    }

    public FlyttetReservasjonDto getFlyttetReservasjon() {
        return flyttetReservasjon;
    }
}
