package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringBehandlingType;
import no.nav.foreldrepenger.los.oppgavekø.FiltreringYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SorteringDto;

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
                    .toList();
        }
        if (!o.getFiltreringYtelseTyper().isEmpty()) {
            fagsakYtelseTyper = o.getFiltreringYtelseTyper().stream()
                    .map(FiltreringYtelseType::getFagsakYtelseType)
                    .toList();
        }
        if (!o.getFiltreringAndreKriterierTyper().isEmpty()) {
            andreKriterier = o.getFiltreringAndreKriterierTyper().stream()
                    .map(AndreKriterierDto::new)
                    .toList();
        }
        this.sortering = new SorteringDto(o.getSortering(), o.getFra(), o.getTil(), o.getFomDato(), o.getTomDato(), o.getErDynamiskPeriode());
        saksbehandlerIdenter = o.getSaksbehandlere().stream()
                .map(Saksbehandler::getSaksbehandlerIdent)
                .toList();

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
