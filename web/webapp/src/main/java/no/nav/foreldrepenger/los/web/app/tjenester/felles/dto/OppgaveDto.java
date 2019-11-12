package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;

import java.time.LocalDateTime;
import java.util.Locale;

public class OppgaveDto {
    private Long id;
    private OppgaveStatusDto status;
    private Long behandlingId;
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
    private Long eksternId;

    public OppgaveDto() {
        // Injiseres i test
    }
    public OppgaveDto(Oppgave oppgave, TpsPersonDto personDto){
        leggTilOppgaveInformasjon(oppgave, null, null);
        leggTilPersonInformasjon(personDto);
    }

    public OppgaveDto(Oppgave oppgave, TpsPersonDto personDto, String flyttetAvNavn) {
        this(oppgave, personDto, null, flyttetAvNavn);
    }

    public OppgaveDto(Oppgave oppgave, TpsPersonDto personDto, String annenSaksbehandlernavn, String flyttetAvNavn){
        leggTilOppgaveInformasjon(oppgave, annenSaksbehandlernavn, flyttetAvNavn);
        leggTilPersonInformasjon(personDto);
    }

    public OppgaveDto(Oppgave oppgave){
        leggTilOppgaveInformasjon(oppgave, null, null);
    }

    private void leggTilOppgaveInformasjon(Oppgave oppgave, String reservertAvNavn, String flyttetAvNavn) {
        this.id = oppgave.getId();
        this.status = OppgaveStatusDto.reservert(oppgave.getReservasjon(), reservertAvNavn, flyttetAvNavn);
        this.saksnummer = oppgave.getFagsakSaksnummer();
        this.behandlingId = oppgave.getBehandlingId();
        this.eksternId = oppgave.getEksternId();
        this.system = oppgave.getSystem();
        this.behandlingStatus = oppgave.getBehandlingStatus();

        this.fagsakYtelseType = oppgave.getFagsakYtelseType();
        this.behandlingstype = oppgave.getBehandlingType();

        //TODO Verdi i DTO skal på sikt skille mellom "Til saksbehandling" og "Til godkjenning"
        this.erTilSaksbehandling = oppgave.getAktiv();

        this.opprettetTidspunkt = oppgave.getBehandlingOpprettet();
        this.behandlingsfrist = oppgave.getBehandlingsfrist();
    }

    private void leggTilPersonInformasjon(TpsPersonDto personDto) {
        this.navn = formaterMedStoreOgSmåBokstaver(personDto.getNavn());
        this.personnummer = personDto.getFnr().getIdent();
    }

    public Long getId() {
        return id;
    }

    public OppgaveStatusDto getStatus() {
        return status;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public Long getEksternId() {
        return eksternId;
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
                ", saksnummer=" + saksnummer+ //$NON-NLS-1$
                ", behandlingId=" + behandlingId+ //$NON-NLS-1$
                ", eksternId=" + eksternId+ //$NON-NLS-1$
                ", navn=" + navn + //$NON-NLS-1$
                ", personnummer=" + personnummer + //$NON-NLS-1$
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
        if (this == o) return true;
        if (!(o instanceof OppgaveDto)) return false;

        OppgaveDto oppgaveDto = (OppgaveDto) o;
        if (saksnummer.equals(oppgaveDto.saksnummer)) {
            return true;
        }
        return behandlingId.equals(oppgaveDto.behandlingId);
    }

    @Override
    public int hashCode() {
        int result = saksnummer.hashCode();
        return 31 * result + behandlingId.hashCode();
    }

    private static String formaterMedStoreOgSmåBokstaver(String tekst) {
        if (tekst == null || (tekst = tekst.trim()).isEmpty()) { // NOSONAR
            return null;
        }
        String skilletegnPattern = "(\\s|[()\\-_.,/])";
        char[] tegn = tekst.toLowerCase(Locale.getDefault()).toCharArray();
        boolean nesteSkalHaStorBokstav = true;
        for (int i = 0; i < tegn.length; i++) {
            boolean erSkilletegn = String.valueOf(tegn[i]).matches(skilletegnPattern);
            if (!erSkilletegn && nesteSkalHaStorBokstav) {
                tegn[i] = Character.toTitleCase(tegn[i]);
            }
            nesteSkalHaStorBokstav = erSkilletegn;
        }
        return new String(tegn);
    }

}
