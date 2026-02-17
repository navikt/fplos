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
import no.nav.foreldrepenger.los.statistikk.StatistikkRepository;
import no.nav.foreldrepenger.los.statistikk.kø.InnslagType;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.StatistikkOppgaveFilter;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteLagreDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteOgAvdelingDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteSaksbehandlerDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
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
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public AvdelingslederSakslisteRestTjeneste(AvdelingslederTjeneste avdelingslederTjeneste,
                                               KøStatistikkTjeneste køStatistikkTjeneste,
                                               StatistikkRepository statistikkRepository,
                                               SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.køStatistikkTjeneste = køStatistikkTjeneste;
        this.statistikkRepository = statistikkRepository;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
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
            .map(of -> new SakslisteDto(of,
                avdelingslederTjeneste.saksbehandlereForOppgaveListe(of).stream().map(saksbehandlerDtoTjeneste::lagKjentOgUkjentSaksbehandler).toList(),
                Optional.ofNullable(statistikkMap.get(of.getId())).map(NøkkeltallRestTjeneste::tilAktiveOgTilgjenglige).orElse(null)))
            .toList();
    }

    @POST
    @Operation(description = "Lag ny saksliste", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public SakslisteIdDto opprettNySaksliste(@NotNull @Parameter(description = "enhet til avdelingsenheten som det skal opprettes ny saksliste for") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return new SakslisteIdDto(avdelingslederTjeneste.lagNyOppgaveFiltrering(avdelingEnhetDto.getAvdelingEnhet()));
    }

    @POST
    @Path("/endre")
    @Operation(description = "Lagre sakslistens navn", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void lagreNavn(@NotNull @Valid SakslisteLagreDto sakslisteLagre) {
        avdelingslederTjeneste.endreEksistrendeOppgaveFilter(sakslisteLagre);
        oppdaterStatistikkForOppgavefilterEtterEndring(sakslisteLagre.sakslisteId());
    }

    @POST
    @Path("/slett")
    @Operation(description = "Fjern saksliste", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void slettSaksliste(@NotNull @Parameter(description = "id til sakslisten som skal slettes") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        avdelingslederTjeneste.hentOppgaveFiltering(sakslisteOgAvdelingDto.sakslisteId().getVerdi()).ifPresent(avdelingslederTjeneste::slettOppgaveFiltrering);
    }

    @POST
    @Path("/saksbehandler")
    @Operation(description = "Legger til eller fjerner koblingen mellom saksliste og saksbehandler", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public void leggSaksbehandlerTilSaksliste(@NotNull @Parameter(description = "Knytning mellom saksbehandler og saksliste") @Valid SakslisteSaksbehandlerDto sakslisteSaksbehandler) {
        var sakslisteId = sakslisteSaksbehandler.sakslisteId().getVerdi();
        var saksbehandlerIdent = sakslisteSaksbehandler.brukerIdent().getVerdi();
        if (sakslisteSaksbehandler.checked()) {
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
            antallTilgjengeligeOppgaver, antallVentendeBehandlinger, 0, 0, InnslagType.SNAPSHOT);
        statistikkRepository.lagreStatistikkOppgaveFilter(statistikkOppgaveFilter);
    }
}
