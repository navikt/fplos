package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.statistikk.StatistikkRepository;
import no.nav.foreldrepenger.los.statistikk.kø.InnslagType;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.StatistikkOppgaveFilter;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteAndreKriterierDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteBehandlingstypeDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteFagsakYtelseTyperDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteNavnDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteOgAvdelingDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteSaksbehandlerDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringIntervallDatoDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringIntervallDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("avdelingsleder/sakslister")
@ApplicationScoped
@Transactional
public class AvdelingslederSakslisteRestTjeneste {

    public static final String AVDELINGSLEDER_SAKSLISTER = "AvdelingslederSakslister";
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private KøStatistikkTjeneste køStatistikkTjeneste;
    private StatistikkRepository statistikkRepository;

    @Inject
    public AvdelingslederSakslisteRestTjeneste(AvdelingslederTjeneste avdelingslederTjeneste,
                                               KøStatistikkTjeneste køStatistikkTjeneste,
                                               StatistikkRepository statistikkRepository) {
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.køStatistikkTjeneste = køStatistikkTjeneste;
        this.statistikkRepository = statistikkRepository;
    }

    AvdelingslederSakslisteRestTjeneste() {
        // CDI
    }

    @GET
    @Operation(description = "Henter alle sakslister for avdeling", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<SakslisteDto> hentAvdelingensSakslister(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        var filtersett = avdelingslederTjeneste.hentOppgaveFiltreringer(avdelingEnhet.getAvdelingEnhet());
        var statistikkMap = statistikkRepository.hentSisteStatistikkForAlleOppgaveFiltre();
        return filtersett.stream()
            .map(of -> new SakslisteDto(of, Optional.ofNullable(statistikkMap.get(of.getId())).map(NøkkeltallRestTjeneste::tilAktiveOgTilgjenglige).orElse(null)))
            .toList();
    }

    @POST
    @Operation(description = "Lag ny saksliste", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public SakslisteIdDto opprettNySaksliste(@NotNull @Parameter(description = "enhet til avdelingsenheten som det skal opprettes ny saksliste for") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return new SakslisteIdDto(avdelingslederTjeneste.lagNyOppgaveFiltrering(avdelingEnhetDto.getAvdelingEnhet()));
    }

    @POST
    @Path("/slett")
    @Operation(description = "Fjern saksliste", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void slettSaksliste(@NotNull @Parameter(description = "id til sakslisten som skal slettes") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        avdelingslederTjeneste.slettOppgaveFiltrering(sakslisteOgAvdelingDto.getSakslisteId().getVerdi());
    }

    @POST
    @Path("/navn")
    @Operation(description = "Lagre sakslistens navn", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreNavn(@NotNull @Parameter(description = "Sakslistens navn") @Valid SakslisteNavnDto sakslisteNavn) {
        avdelingslederTjeneste.giListeNyttNavn(sakslisteNavn.getSakslisteId(), sakslisteNavn.getNavn());
    }

    @POST
    @Path("/behandlingstype")
    @Operation(description = "Lagre sakslistens behandlingstype", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreBehandlingstype(@NotNull @Parameter(description = "Sakslistens behandlingstype") @Valid SakslisteBehandlingstypeDto sakslisteBehandlingstype) {
        avdelingslederTjeneste.endreFiltreringBehandlingType(sakslisteBehandlingstype.getSakslisteId(), sakslisteBehandlingstype.getBehandlingType(),
            sakslisteBehandlingstype.isChecked());
        oppdaterStatistikkForOppgavefilterEtterEndring(sakslisteBehandlingstype.getSakslisteId());
    }

    @POST
    @Path("/ytelsetyper")
    @Operation(description = "Lagre behandlingstyper", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreFagsakYtelseTyper(@NotNull @Parameter(description = "Ytelsestyper") @Valid SakslisteFagsakYtelseTyperDto dto) {
        avdelingslederTjeneste.endreFagsakYtelseType(dto.getSakslisteId(), dto.getFagsakYtelseType(), dto.isChecked());
        oppdaterStatistikkForOppgavefilterEtterEndring(dto.getSakslisteId());
    }


    @POST
    @Path("/andre-kriterier")
    @Operation(description = "Lagre sakslistens 'Andre kriterier'", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreAndreKriterierType(@NotNull @Parameter(description = "Sakslistens 'andre kriterier'") @Valid SakslisteAndreKriterierDto sakslisteAndreKriterierDto) {
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(sakslisteAndreKriterierDto.getSakslisteId(),
            sakslisteAndreKriterierDto.getAndreKriterierType(), sakslisteAndreKriterierDto.isChecked(), sakslisteAndreKriterierDto.isInkluder());
        oppdaterStatistikkForOppgavefilterEtterEndring(sakslisteAndreKriterierDto.getSakslisteId());
    }

    @POST
    @Path("/sortering")
    @Operation(description = "Sett sakslistens sortering", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreSortering(@NotNull @Parameter(description = "Sakslistens sortering") @Valid SakslisteSorteringDto sakslisteSortering) {
        avdelingslederTjeneste.settSortering(sakslisteSortering.getSakslisteId(), sakslisteSortering.getSakslisteSorteringValg());
        oppdaterStatistikkForOppgavefilterEtterEndring(sakslisteSortering.getSakslisteId());
    }

    @POST
    @Path("/sortering-tidsintervall-dato")
    @Operation(description = "Sett sakslistens sorteringintervall ved start og slutt datoer", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreSorteringTidsintervallDato(@NotNull @Parameter(description = "Sakslistens sorteringsintervall gitt datoer") @Valid SakslisteSorteringIntervallDatoDto sakslisteSorteringIntervallDato) {
        avdelingslederTjeneste.settSorteringTidsintervallDato(sakslisteSorteringIntervallDato.getSakslisteId(),
            sakslisteSorteringIntervallDato.getFomDato(), sakslisteSorteringIntervallDato.getTomDato());
        oppdaterStatistikkForOppgavefilterEtterEndring(sakslisteSorteringIntervallDato.getSakslisteId());
    }

    @POST
    @Path("/sortering-numerisk-intervall")
    @Operation(description = "Sett sakslistens sorteringsintervall", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreSorteringTidsintervall(@NotNull @Parameter(description = "Intervall som filtrererer/sorterer numerisk") @Valid SakslisteSorteringIntervallDto intervall) {
        var periodefilter = intervall.getPeriodefilter() == null ? Periodefilter.RELATIV_PERIODE_DAGER : intervall.getPeriodefilter();
        avdelingslederTjeneste.settSorteringNumeriskIntervall(intervall.getSakslisteId(), intervall.getFra(), intervall.getTil(), periodefilter);
        oppdaterStatistikkForOppgavefilterEtterEndring(intervall.getSakslisteId());
    }

    @POST
    @Path("/sortering-tidsintervall-type")
    @Operation(description = "Sett sakslistens sorteringsintervall i dager", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreSorteringTidsintervallValg(@NotNull @Parameter(description = "id til sakslisten") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        var sakslisteId = sakslisteOgAvdelingDto.getSakslisteId().getVerdi();
        var oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteId);
        oppgaveFiltrering.ifPresentOrElse(of -> {
            var periodefilter = of.getPeriodefilter()
                == Periodefilter.FAST_PERIODE ? Periodefilter.RELATIV_PERIODE_DAGER : Periodefilter.FAST_PERIODE;
            avdelingslederTjeneste.settSorteringTidsintervallValg(sakslisteId, periodefilter);
        }, () -> {
            throw new IllegalArgumentException("Fant ikke listen");
        });
        oppdaterStatistikkForOppgavefilterEtterEndring(sakslisteId);
    }

    @POST
    @Path("/saksbehandler")
    @Operation(description = "Legger til eller fjerner koblingen mellom saksliste og saksbehandler", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void leggSaksbehandlerTilSaksliste(@NotNull @Parameter(description = "Knytning mellom saksbehandler og saksliste") @Valid SakslisteSaksbehandlerDto sakslisteSaksbehandler) {
        var sakslisteId = sakslisteSaksbehandler.getSakslisteId();
        var saksbehandlerIdent = sakslisteSaksbehandler.getBrukerIdent().getVerdi();
        if (sakslisteSaksbehandler.isChecked()) {
            avdelingslederTjeneste.leggSaksbehandlerTilListe(sakslisteId, saksbehandlerIdent);
        } else {
            avdelingslederTjeneste.fjernSaksbehandlerFraListe(sakslisteId, saksbehandlerIdent);
        }
    }

    private void oppdaterStatistikkForOppgavefilterEtterEndring(Long sakslisteId) {
        var antallOppgaver = køStatistikkTjeneste.hentAntallOppgaver(sakslisteId);
        var antallTilgjengeligeOppgaver = køStatistikkTjeneste.hentAntallTilgjengeligeOppgaverFor(sakslisteId);
        var antallVentendeBehandlinger = køStatistikkTjeneste.hentAntallVentendeBehandlingerFor(sakslisteId);
        var statistikkOppgaveFilter = new StatistikkOppgaveFilter(sakslisteId, System.currentTimeMillis(), LocalDate.now(), antallOppgaver,
            antallTilgjengeligeOppgaver, antallVentendeBehandlinger, InnslagType.SNAPSHOT);
        statistikkRepository.lagreStatistikkOppgaveFilter(statistikkOppgaveFilter);
    }
}
