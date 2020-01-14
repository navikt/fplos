package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SorteringDto;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringBehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FiltreringYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SakslisteDto {

    private SakslisteIdDto sakslisteId;
    private String navn;
    private List<BehandlingType> behandlingTyper = new ArrayList<>();
    private LocalDate sistEndret;
    private SorteringDto sortering;
    private List<FagsakYtelseType> fagsakYtelseTyper = new ArrayList<>();
    private List<AndreKriterierDto> andreKriterier = new ArrayList<>();
    private List<String> saksbehandlerIdenter;
    private Integer antallBehandlinger;

    public SakslisteDto(OppgaveFiltrering o, Integer antallBehandlinger) {
        sakslisteId = new SakslisteIdDto(o.getId());
        navn = o.getNavn();
        sistEndret = o.getEndretTidspunkt() == null
                ? o.getOpprettetTidspunkt().toLocalDate()
                : o.getEndretTidspunkt().toLocalDate();
        if (!o.getFiltreringBehandlingTyper().isEmpty()) {
            behandlingTyper = o.getFiltreringBehandlingTyper().stream()
                    .map(FiltreringBehandlingType::getBehandlingType)
                    .collect(Collectors.toList());
        }
        if (!o.getFiltreringYtelseTyper().isEmpty()) {
            fagsakYtelseTyper = o.getFiltreringYtelseTyper().stream()
                    .map(FiltreringYtelseType::getFagsakYtelseType)
                    .collect(Collectors.toList());
        }
        if (!o.getFiltreringAndreKriterierTyper().isEmpty()) {
            andreKriterier = o.getFiltreringAndreKriterierTyper().stream()
                    .map(AndreKriterierDto::new)
                    .collect(Collectors.toList());
        }
        this.sortering = new SorteringDto(o.getSortering(), o.getFomDager(), o.getTomDager(), o.getFomDato(), o.getTomDato(), o.getErDynamiskPeriode());
        saksbehandlerIdenter = o.getSaksbehandlere().stream()
                .map(Saksbehandler::getSaksbehandlerIdent)
                .collect(Collectors.toList());

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
