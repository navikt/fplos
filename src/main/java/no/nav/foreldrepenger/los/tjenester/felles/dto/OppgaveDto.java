package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;

public class OppgaveDto {
    @NotNull private Long id;
    @NotNull private String saksnummer;
    @NotNull private String navn;
    @NotNull private Fagsystem system;
    @NotNull private String personnummer;
    @NotNull private BehandlingType behandlingstype;
    @NotNull private FagsakYtelseType fagsakYtelseType;
    @NotNull private Boolean erTilSaksbehandling;
    @NotNull private LocalDateTime opprettetTidspunkt;
    @NotNull private LocalDateTime behandlingsfrist;
    @NotNull private BehandlingId behandlingId;
    @NotNull private Set<AndreKriterierType> andreKriterier;
    @NotNull private ReservasjonStatusDto reservasjonStatus;

    OppgaveDto(OppgaveDto other) {
        this.id = other.getId();
        this.reservasjonStatus = other.getReservasjonStatus();
        this.saksnummer = other.getSaksnummer();
        this.navn = other.getNavn();
        this.system = other.getSystem();
        this.personnummer = other.getPersonnummer();
        this.behandlingstype = other.getBehandlingstype();
        this.fagsakYtelseType = other.getFagsakYtelseType();
        this.erTilSaksbehandling = other.getErTilSaksbehandling();
        this.opprettetTidspunkt = other.getOpprettetTidspunkt();
        this.behandlingsfrist = other.getBehandlingsfrist();
        this.behandlingId = BehandlingId.fromUUID(other.getBehandlingId());
        this.andreKriterier = other.getAndreKriterier();
    }

    OppgaveDto(Oppgave oppgave, Person personDto, ReservasjonStatusDto reservasjonStatus) {
        leggTilOppgaveInformasjon(oppgave, reservasjonStatus);
        leggTilPersonInformasjon(personDto);
    }

    private void leggTilOppgaveInformasjon(Oppgave oppgave, ReservasjonStatusDto reservasjonStatus) {
        this.id = oppgave.getId();
        this.saksnummer = oppgave.getSaksnummer().getVerdi();
        this.behandlingId = oppgave.getBehandlingId();
        this.system = oppgave.getSystem();
        this.fagsakYtelseType = oppgave.getFagsakYtelseType();
        this.behandlingstype = oppgave.getBehandlingType();
        this.erTilSaksbehandling = oppgave.getAktiv();
        this.opprettetTidspunkt = oppgave.getBehandlingOpprettet();
        this.behandlingsfrist = oppgave.getBehandlingsfrist();
        this.andreKriterier = oppgave.getOppgaveEgenskaper().stream()
            .map(OppgaveEgenskap::getAndreKriterierType)
            .collect(Collectors.toSet());
        this.reservasjonStatus = reservasjonStatus;
    }

    private void leggTilPersonInformasjon(Person person) {
        this.navn = person.navn();
        this.personnummer = person.fødselsnummer().value();
    }

    public Long getId() {
        return id;
    }

    public ReservasjonStatusDto getReservasjonStatus() {
        return reservasjonStatus;
    }

    public UUID getBehandlingId() {
        return behandlingId.toUUID();
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getNavn() {
        return navn;
    }

    public Fagsystem getSystem() {
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

    public Boolean getErTilSaksbehandling() {
        return erTilSaksbehandling;
    }

    public Set<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String toString() {
        return "OppgaveDto{"
            + "id=" + id
            + ", saksnummer='" + saksnummer + '\''
            + ", navn='" + navn + '\''
            + ", system='" + system + '\''
            + ", personnummer='***'"
            + ", behandlingstype=" + behandlingstype
            + ", fagsakYtelseType=" + fagsakYtelseType
            + ", erTilSaksbehandling=" + erTilSaksbehandling
            + ", opprettetTidspunkt=" + opprettetTidspunkt
            + ", behandlingsfrist=" + behandlingsfrist
            + ", behandlingId=" + behandlingId
            + ", andreKriterier=" + andreKriterier
            + ", reservasjonStatus=" + reservasjonStatus + '}';
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
