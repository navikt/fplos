package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste;

import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteAndreKriterierDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteBehandlingstypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteFagsakYtelseTypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteFagsakYtelseTyperDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteNavnDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringIntervallDatoDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringIntervallDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
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
    private OppgaveKøTjeneste oppgaveKøTjeneste;

    @Inject
    public AvdelingslederSakslisteRestTjeneste(AvdelingslederTjeneste avdelingslederTjeneste, OppgaveKøTjeneste oppgaveKøTjeneste) {
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
    }

    AvdelingslederSakslisteRestTjeneste() {
        // CDI
    }

    @GET
    @Operation(description = "Henter alle sakslister for avdeling", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<SakslisteDto> hentAvdelingensSakslister(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        var filtersett = avdelingslederTjeneste.hentOppgaveFiltreringer(avdelingEnhet.getAvdelingEnhet());
        return filtersett.stream().map(o -> new SakslisteDto(o, oppgaveKøTjeneste.hentAntallOppgaver(o.getId(), true))).toList();
    }

    @POST
    @Operation(description = "Lag ny saksliste", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public SakslisteIdDto opprettNySaksliste(@NotNull @Parameter(description = "enhet til avdelingsenheten som det skal opprettes ny saksliste for") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return new SakslisteIdDto(avdelingslederTjeneste.lagNyOppgaveFiltrering(avdelingEnhetDto.getAvdelingEnhet()));
    }

    //FIXME (TOR) burde ein heller brukt @DELETE her?
    @POST
    @Path("/slett")
    @Operation(description = "Fjern saksliste", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING)
    public void slettSaksliste(@NotNull @Parameter(description = "id til sakslisten som skal slettes") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        avdelingslederTjeneste.slettOppgaveFiltrering(sakslisteOgAvdelingDto.getSakslisteId().getVerdi());
    }

    @POST
    @Path("/navn")
    @Operation(description = "Lagre sakslistens navn", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreNavn(@NotNull @Parameter(description = "Sakslistens navn") @Valid SakslisteNavnDto sakslisteNavn) {
        avdelingslederTjeneste.giListeNyttNavn(sakslisteNavn.getSakslisteId(), sakslisteNavn.getNavn());
    }

    @POST
    @Path("/behandlingstype")
    @Operation(description = "Lagre sakslistens behandlingstype", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreBehandlingstype(@NotNull @Parameter(description = "Sakslistens behandlingstype") @Valid SakslisteBehandlingstypeDto sakslisteBehandlingstype) {
        avdelingslederTjeneste.endreFiltreringBehandlingType(sakslisteBehandlingstype.getSakslisteId(), sakslisteBehandlingstype.getBehandlingType(),
            sakslisteBehandlingstype.isChecked());
    }

    @POST
    @Path("/ytelsetype")
    @Operation(description = "Lagre sakslistens behandlingstype", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreFagsakYtelseType(@NotNull @Parameter(description = "Sakslistens ytelsetype") @Valid SakslisteFagsakYtelseTypeDto sakslisteFagsakYtelseTypeDto) {
        avdelingslederTjeneste.endreFiltreringYtelseType(sakslisteFagsakYtelseTypeDto.getSakslisteId(),
            sakslisteFagsakYtelseTypeDto.getFagsakYtelseType());
    }


    @POST
    @Path("/ytelsetyper")
    @Operation(description = "Lagre behandlingstyper", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreFagsakYtelseTyper(@NotNull @Parameter(description = "Ytelsestyper") @Valid SakslisteFagsakYtelseTyperDto dto) {
        avdelingslederTjeneste.endreFyt(dto.getSakslisteId(), dto.getFagsakYtelseType(), dto.isChecked());
    }


    @POST
    @Path("/andre-kriterier")
    @Operation(description = "Lagre sakslistens 'Andre kriterier'", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreAndreKriterierType(@NotNull @Parameter(description = "Sakslistens 'andre kriterier'") @Valid SakslisteAndreKriterierDto sakslisteAndreKriterierDto) {
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(sakslisteAndreKriterierDto.getSakslisteId(),
            sakslisteAndreKriterierDto.getAndreKriterierType(), sakslisteAndreKriterierDto.isChecked(), sakslisteAndreKriterierDto.isInkluder());
    }

    @POST
    @Path("/sortering")
    @Operation(description = "Sett sakslistens sortering", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreSortering(@NotNull @Parameter(description = "Sakslistens sortering") @Valid SakslisteSorteringDto sakslisteSortering) {
        avdelingslederTjeneste.settSortering(sakslisteSortering.getSakslisteId(), sakslisteSortering.getSakslisteSorteringValg());
    }

    @POST
    @Path("/sortering-tidsintervall-dato")
    @Operation(description = "Sett sakslistens sorteringintervall ved start og slutt datoer", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreSorteringTidsintervallDato(@NotNull @Parameter(description = "Sakslistens sorteringsintervall gitt datoer") @Valid SakslisteSorteringIntervallDatoDto sakslisteSorteringIntervallDato) {
        avdelingslederTjeneste.settSorteringTidsintervallDato(sakslisteSorteringIntervallDato.getSakslisteId(),
            sakslisteSorteringIntervallDato.getFomDato(), sakslisteSorteringIntervallDato.getTomDato());
    }

    @POST
    @Path("/sortering-tidsintervall-dager") //Bruker ikke bare til intervall på dager
    @Operation(description = "Sett sakslistens sorteringsintervall", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreSorteringTidsintervallDager(@NotNull @Parameter(description = "Sakslistens sortering") @Valid SakslisteSorteringIntervallDto intervall) {
        lagreSorteringTidsintervall(intervall);
    }

    @POST
    @Path("/sortering-numerisk-intervall")
    @Operation(description = "Sett sakslistens sorteringsintervall", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreSorteringTidsintervall(@NotNull @Parameter(description = "Intervall som filtrererer/sorterer numerisk") @Valid SakslisteSorteringIntervallDto intervall) {
        avdelingslederTjeneste.settSorteringNumeriskIntervall(intervall.getSakslisteId(), intervall.getFra(), intervall.getTil());
    }

    @POST
    @Path("/sortering-tidsintervall-type")
    @Operation(description = "Sett sakslistens sorteringsintervall i dager", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void lagreSorteringTidsintervallValg(@NotNull @Parameter(description = "id til sakslisten") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        var sakslisteId = sakslisteOgAvdelingDto.getSakslisteId().getVerdi();
        var oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteId);
        oppgaveFiltrering.ifPresentOrElse(of -> avdelingslederTjeneste.settSorteringTidsintervallValg(sakslisteId, !of.getErDynamiskPeriode()),
            () -> {
                throw new IllegalArgumentException("Fant ikke listen");
            });
    }

    @POST
    @Path("/saksbehandler")
    @Operation(description = "Legger til eller fjerner koblingen mellom saksliste og saksbehandler", tags = AVDELINGSLEDER_SAKSLISTER)
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void leggSaksbehandlerTilSaksliste(@NotNull @Parameter(description = "Knytning mellom saksbehandler og saksliste") @Valid SakslisteSaksbehandlerDto sakslisteSaksbehandler) {
        var sakslisteId = sakslisteSaksbehandler.getSakslisteId();
        var saksbehandlerIdent = sakslisteSaksbehandler.getBrukerIdent().getVerdi();
        if (sakslisteSaksbehandler.isChecked()) {
            avdelingslederTjeneste.leggSaksbehandlerTilListe(sakslisteId, saksbehandlerIdent);
        } else {
            avdelingslederTjeneste.fjernSaksbehandlerFraListe(sakslisteId, saksbehandlerIdent);
        }
    }
}
