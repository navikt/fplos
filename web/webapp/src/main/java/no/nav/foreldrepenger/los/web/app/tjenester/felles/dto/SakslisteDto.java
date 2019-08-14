package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SorteringDto;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;

public class SakslisteDto {

    private SakslisteIdDto sakslisteId;
    private String navn;
    private List<BehandlingType> behandlingTyper = new ArrayList<>();
    private LocalDate sistEndret;
    private SorteringDto sortering;
    private List<FagsakYtelseType> fagsakYtelseTyper = new ArrayList<>();
    private List<AndreKriterierDto> andreKriterier = new ArrayList<>();
    private List<String> saksbehandlerIdenter = new ArrayList<>();

    public SakslisteDto(OppgaveFiltrering o) {
        sakslisteId = new SakslisteIdDto(o.getId());
        navn = o.getNavn();
        sistEndret = null == o.getEndretTidspunkt() ? o.getOpprettetTidspunkt().toLocalDate() : o.getEndretTidspunkt().toLocalDate();
        if(!o.getFiltreringBehandlingTyper().isEmpty()){
            behandlingTyper = o.getFiltreringBehandlingTyper().stream().map(filtreringbt -> filtreringbt.getBehandlingType()).collect(Collectors.toList());
        }
        if(!o.getFiltreringYtelseTyper().isEmpty()){
            fagsakYtelseTyper = o.getFiltreringYtelseTyper().stream().map(filtreringbt -> filtreringbt.getFagsakYtelseType()).collect(Collectors.toList());
        }
        if(!o.getFiltreringAndreKriterierTyper().isEmpty()){
            andreKriterier = o.getFiltreringAndreKriterierTyper().stream().map(filtreringandrekrit -> new AndreKriterierDto(filtreringandrekrit)).collect(Collectors.toList());
        }
        this.sortering = new SorteringDto( o.getSortering(), o.getFomDager(), o.getTomDager(), o.getFomDato(), o.getTomDato(), o.getErDynamiskPeriode());
        saksbehandlerIdenter = o.getSaksbehandlere().stream().map(s -> s.getSaksbehandlerIdent()).collect(Collectors.toList());
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
}
