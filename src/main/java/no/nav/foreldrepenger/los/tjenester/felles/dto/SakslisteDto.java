package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SorteringDto;

public class SakslisteDto {

    private SakslisteIdDto sakslisteId;
    private String navn;
    private LocalDate sistEndret;
    private SorteringDto sortering;
    private List<BehandlingType> behandlingTyper;
    private List<FagsakYtelseType> fagsakYtelseTyper;
    private List<AndreKriterierDto> andreKriterier;
    private List<String> saksbehandlerIdenter;
    private Integer antallBehandlinger;

    public SakslisteDto(OppgaveFiltrering o, Integer antallBehandlinger) {
        sakslisteId = new SakslisteIdDto(o.getId());
        navn = o.getNavn();
        sistEndret = o.getEndretTidspunkt() == null ? o.getOpprettetTidspunkt().toLocalDate() : o.getEndretTidspunkt().toLocalDate();
        behandlingTyper = o.getBehandlingTyper();
        fagsakYtelseTyper = o.getFagsakYtelseTyper();
        andreKriterier = o.getFiltreringAndreKriterierTyper().stream().map(AndreKriterierDto::new).toList();
        sortering = new SorteringDto(o.getSortering(), o.getFra(), o.getTil(), o.getFomDato(), o.getTomDato(), o.getErDynamiskPeriode());
        saksbehandlerIdenter = o.getSaksbehandlere().stream().map(Saksbehandler::getSaksbehandlerIdent).toList();
        this.antallBehandlinger = antallBehandlinger;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public String getNavn() {
        return navn;
    }

    public List<BehandlingType> getBehandlingTyper() {
        return behandlingTyper;
    }

    public LocalDate getSistEndret() {
        return sistEndret;
    }

    public SorteringDto getSortering() {
        return sortering;
    }

    public List<FagsakYtelseType> getFagsakYtelseTyper() {
        return fagsakYtelseTyper;
    }

    public List<AndreKriterierDto> getAndreKriterier() {
        return andreKriterier;
    }

    public List<String> getSaksbehandlerIdenter() {
        return saksbehandlerIdenter;
    }

    public Integer getAntallBehandlinger() {
        return antallBehandlinger;
    }
}
