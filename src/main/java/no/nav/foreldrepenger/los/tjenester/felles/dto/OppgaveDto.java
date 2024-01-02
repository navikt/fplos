package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

public class OppgaveDto {
    private Long id;
    private ReservasjonStatusDto status;
    private Long saksnummer;
    private String navn;
    private String system;
    private String personnummer;
    private BehandlingType behandlingstype;
    private FagsakYtelseType fagsakYtelseType;
    private BehandlingStatus behandlingStatus;
    private Boolean erTilSaksbehandling;
    private LocalDateTime opprettetTidspunkt;
    private LocalDateTime behandlingsfrist;
    private BehandlingId behandlingId;

    OppgaveDto(Oppgave oppgave, Person personDto, ReservasjonStatusDto oppgaveStatus) {
        leggTilOppgaveInformasjon(oppgave, oppgaveStatus);
        leggTilPersonInformasjon(personDto);
    }

    private void leggTilOppgaveInformasjon(Oppgave oppgave, ReservasjonStatusDto status) {
        this.id = oppgave.getId();
        this.status = status;
        this.saksnummer = oppgave.getFagsakSaksnummer();
        this.behandlingId = oppgave.getBehandlingId();
        this.system = oppgave.getSystem();
        this.behandlingStatus = oppgave.getBehandlingStatus();
        this.fagsakYtelseType = oppgave.getFagsakYtelseType();
        this.behandlingstype = oppgave.getBehandlingType();
        this.erTilSaksbehandling = oppgave.getAktiv();
        this.opprettetTidspunkt = oppgave.getBehandlingOpprettet();
        this.behandlingsfrist = oppgave.getBehandlingsfrist();
    }

    private void leggTilPersonInformasjon(Person person) {
        this.navn = person.getNavn();
        this.personnummer = person.getFødselsnummer().value();
    }

    public Long getId() {
        return id;
    }

    public ReservasjonStatusDto getStatus() {
        return status;
    }

    public UUID getBehandlingId() {
        return behandlingId.toUUID();
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public String getNavn() {
        return navn;
    }

    public String getSystem() {
        return system;
    }

    public String getPersonnummer() {
        return personnummer;
    }

    public BehandlingType getBehandlingstype() {
        return behandlingstype;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public LocalDateTime getBehandlingsfrist() {
        return behandlingsfrist;
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public BehandlingStatus getBehandlingStatus() {
        return behandlingStatus;
    }


    public Boolean getErTilSaksbehandling() {
        return erTilSaksbehandling;
    }

    @Override
    public String toString() {
        return "<id=" + id + //$NON-NLS-1$
            ", status=" + status.isErReservert() + //$NON-NLS-1$
            ", saksnummer=" + saksnummer + //$NON-NLS-1$
            ", behandlingId=" + behandlingId + //$NON-NLS-1$
            ", system=" + system + //$NON-NLS-1$
            ", behandlingstype=" + behandlingstype + //$NON-NLS-1$
            ", opprettetTidspunkt=" + opprettetTidspunkt + //$NON-NLS-1$
            ", behandlingsfrist=" + behandlingsfrist + //$NON-NLS-1$
            ", fagsakYtelseType=" + fagsakYtelseType + //$NON-NLS-1$
            ", behandlingStatus=" + behandlingStatus + //$NON-NLS-1$
            ", erTilSaksbehandling=" + erTilSaksbehandling + //$NON-NLS-1$
            ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OppgaveDto)) {
            return false;
        }

        var oppgaveDto = (OppgaveDto) o;
        if (saksnummer.equals(oppgaveDto.saksnummer)) {
            return true;
        }
        return behandlingId.equals(oppgaveDto.behandlingId);
    }

    @Override
    public int hashCode() {
        var result = saksnummer.hashCode();
        return 31 * result + behandlingId.hashCode();
    }

}
