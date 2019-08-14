package no.nav.fplos.foreldrepengerbehandling.dto.fagsak;

import java.time.LocalDate;
import java.time.LocalDateTime;

import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

public class FagsakDto {

    private Long saksnummer;
    private FagsakYtelseType sakstype;
    private FagsakStatus status;
    private PersonDto person;
    private LocalDateTime opprettet;
    private LocalDateTime endret;
    private LocalDate barnFodt;

    public FagsakDto() {
        // Injiseres i test
    }

    public FagsakDto(Long saksnummer, FagsakYtelseType sakstype, FagsakStatus status, PersonDto person, LocalDateTime opprettet, LocalDateTime endret, LocalDate barnFodt) {
        this.saksnummer = saksnummer;
        this.sakstype = sakstype;
        this.status = status;
        this.person = person;
        this.opprettet = opprettet;
        this.endret = endret;
        this.barnFodt = barnFodt;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public FagsakYtelseType getSakstype() {
        return sakstype;
    }

    public FagsakStatus getStatus() {
        return status;
    }

    public PersonDto getPerson() {
        return person;
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
    }

    public LocalDateTime getEndret() {
        return endret;
    }

    public LocalDate getBarnFodt() {
        return barnFodt;
    }

    @Override
    public String toString() {
        return "<saksnummer=" + saksnummer + //$NON-NLS-1$
                ", sakstype=" + sakstype + //$NON-NLS-1$
                ", status=" + status + //$NON-NLS-1$
                ", barnFodt=" + barnFodt + //$NON-NLS-1$
                ", person=" + person + //$NON-NLS-1$
                ", opprettet=" + opprettet + //$NON-NLS-1$
                ", endret=" + endret + //$NON-NLS-1$
                ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FagsakDto)) return false;

        FagsakDto fagsakDto = (FagsakDto) o;
        return saksnummer.equals(fagsakDto.saksnummer);
    }

    @Override
    public int hashCode() {
        return 31 * saksnummer.hashCode();
    }

}
