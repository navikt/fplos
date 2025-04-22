package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;

public class OppgaveDto {
    private Long id;
    private ReservasjonStatusDto status;
    private String saksnummer;
    private String saksnummerString;
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
    private Set<AndreKriterierType> andreKriterier;

    OppgaveDto(Oppgave oppgave, Person personDto, ReservasjonStatusDto oppgaveStatus) {
        leggTilOppgaveInformasjon(oppgave, oppgaveStatus);
        leggTilPersonInformasjon(personDto);
    }

    private void leggTilOppgaveInformasjon(Oppgave oppgave, ReservasjonStatusDto status) {
        this.id = oppgave.getId();
        this.status = status;
        this.saksnummer = oppgave.getSaksnummer();
        this.saksnummerString = oppgave.getSaksnummer();
        this.behandlingId = oppgave.getBehandlingId();
        this.system = oppgave.getSystem();
        this.behandlingStatus = oppgave.getBehandlingStatus();
        this.fagsakYtelseType = oppgave.getFagsakYtelseType();
        this.behandlingstype = oppgave.getBehandlingType();
        this.erTilSaksbehandling = oppgave.getAktiv();
        this.opprettetTidspunkt = oppgave.getBehandlingOpprettet();
        this.behandlingsfrist = oppgave.getBehandlingsfrist();
        this.andreKriterier = oppgave.getOppgaveEgenskaper().stream()
            .filter(OppgaveEgenskap::getAktiv)
            .map(OppgaveEgenskap::getAndreKriterierType)
            .collect(Collectors.toSet());
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

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getSaksnummerString() {
        return saksnummerString;
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

    public Set<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String toString() {
        return "<id=" + id +
            ", status=" + status.isErReservert() +
            ", saksnummer=" + saksnummer +
            ", behandlingId=" + behandlingId +
            ", system=" + system +
            ", behandlingstype=" + behandlingstype +
            ", opprettetTidspunkt=" + opprettetTidspunkt +
            ", behandlingsfrist=" + behandlingsfrist +
            ", fagsakYtelseType=" + fagsakYtelseType +
            ", behandlingStatus=" + behandlingStatus +
            ", erTilSaksbehandling=" + erTilSaksbehandling +
            ", andreKriterier=" + andreKriterier +
            ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OppgaveDto oppgaveDto)) {
            return false;
        }
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
