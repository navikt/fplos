package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteAndreKriterierDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteBehandlingstypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteFagsakYtelseTypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteNavnDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringIntervallDatoDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringIntervallDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@Path("avdelingsleder/sakslister")
@ApplicationScoped
@Transactional
public class AvdelingslederSakslisteRestTjeneste {

    private AvdelingslederTjeneste avdelingslederTjeneste;
    private OppgaveKøTjeneste oppgaveKøTjeneste;

    @Inject
    public AvdelingslederSakslisteRestTjeneste(AvdelingslederTjeneste avdelingslederTjeneste, OppgaveKøTjeneste oppgaveKøTjeneste) {
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
    }

    public AvdelingslederSakslisteRestTjeneste() {
        //NOSONAR
    }

    @GET
    @Produces("application/json")
    @Operation(description = "Henter alle sakslister for avdeling", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<SakslisteDto> hentAvdelingensSakslister(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        var filtersett = avdelingslederTjeneste.hentOppgaveFiltreringer(avdelingEnhet.getAvdelingEnhet());
        return filtersett.stream()
                .map(o-> new SakslisteDto(o, oppgaveKøTjeneste.hentAntallOppgaver(o.getId(), true)))
                .collect(Collectors.toList());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Lag ny saksliste", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public SakslisteIdDto opprettNySaksliste(@NotNull @Parameter(description = "enhet til avdelingsenheten som det skal opprettes ny saksliste for") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return new SakslisteIdDto(avdelingslederTjeneste.lagNyOppgaveFiltrering(avdelingEnhetDto.getAvdelingEnhet()));
    }

    //FIXME (TOR) burde ein heller brukt @DELETE her?
    @POST
    @Path("/slett")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fjern saksliste", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING)
//    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void slettSaksliste(@NotNull @Parameter(description = "id til sakslisten som skal slettes") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        avdelingslederTjeneste.slettOppgaveFiltrering(sakslisteOgAvdelingDto.getSakslisteId().getVerdi());
    }

    @POST
    @Path("/navn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Lagre sakslistens navn", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreNavn(@NotNull @Parameter(description = "Sakslistens navn") @Valid SakslisteNavnDto sakslisteNavn) {
        avdelingslederTjeneste.giListeNyttNavn(sakslisteNavn.getSakslisteId(),sakslisteNavn.getNavn());
    }

    @POST
    @Path("/behandlingstype")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Lagre sakslistens behandlingstype", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreBehandlingstype(@NotNull @Parameter(description = "Sakslistens behandlingstype") @Valid SakslisteBehandlingstypeDto sakslisteBehandlingstype) {
        avdelingslederTjeneste.endreFiltreringBehandlingType(sakslisteBehandlingstype.getSakslisteId()
                ,sakslisteBehandlingstype.getBehandlingType(), sakslisteBehandlingstype.isChecked());
    }

    @POST
    @Path("/ytelsetype")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Lagre sakslistens behandlingstype", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreFagsakYtelseType(@NotNull @Parameter(description = "Sakslistens ytelsetype") @Valid SakslisteFagsakYtelseTypeDto sakslisteFagsakYtelseTypeDto) {
        avdelingslederTjeneste.endreFiltreringYtelseType(
                sakslisteFagsakYtelseTypeDto.getSakslisteId(),
                sakslisteFagsakYtelseTypeDto.getFagsakYtelseType());
    }

    @POST
    @Path("/andre-kriterier")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Lagre sakslistens 'Andre kriterier'", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreAndreKriterierType(@NotNull @Parameter(description = "Sakslistens 'andre kriterier'") @Valid SakslisteAndreKriterierDto sakslisteAndreKriterierDto) {
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(
                    sakslisteAndreKriterierDto.getSakslisteId(),
                    sakslisteAndreKriterierDto.getAndreKriterierType(),
                    sakslisteAndreKriterierDto.isChecked(),
                    sakslisteAndreKriterierDto.isInkluder());
    }

    @POST
    @Path("/sortering")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sett sakslistens sortering", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSortering(@NotNull @Parameter(description = "Sakslistens sortering") @Valid SakslisteSorteringDto sakslisteSortering) {
        avdelingslederTjeneste.settSortering(sakslisteSortering.getSakslisteId(),
                sakslisteSortering.getSakslisteSorteringValg());
    }

    @POST
    @Path("/sortering-tidsintervall-dato")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sett sakslistens sorteringintervall ved start og slutt datoer", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervallDato(@NotNull @Parameter(description = "Sakslistens sorteringsintervall gitt datoer") @Valid SakslisteSorteringIntervallDatoDto sakslisteSorteringIntervallDato) {
        avdelingslederTjeneste.settSorteringTidsintervallDato(sakslisteSorteringIntervallDato.getSakslisteId(),
                sakslisteSorteringIntervallDato.getFomDato(),
                sakslisteSorteringIntervallDato.getTomDato());
    }

    @POST
    @Path("/sortering-tidsintervall-dager") //Bruker ikke bare til intervall på dager
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sett sakslistens sorteringsintervall", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervallDager(@NotNull @Parameter(description = "Sakslistens sortering") @Valid SakslisteSorteringIntervallDto intervall) {
        lagreSorteringTidsintervall(intervall);
    }

    @POST
    @Path("/sortering-numerisk-intervall")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sett sakslistens sorteringsintervall", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervall(@NotNull @Parameter(description = "Intervall som filtrererer/sorterer numerisk") @Valid SakslisteSorteringIntervallDto intervall) {
        avdelingslederTjeneste.settSorteringNumeriskIntervall(intervall.getSakslisteId(), intervall.getFra(), intervall.getTil());
    }

    @POST
    @Path("/sortering-tidsintervall-type")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sett sakslistens sorteringsintervall i dager", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervallValg(@NotNull @Parameter(description = "id til sakslisten") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        var sakslisteId = sakslisteOgAvdelingDto.getSakslisteId().getVerdi();
        var oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteId);
        oppgaveFiltrering.ifPresentOrElse(
                of -> avdelingslederTjeneste.settSorteringTidsintervallValg(sakslisteId, !of.getErDynamiskPeriode()),
                () -> { throw new IllegalArgumentException("Fant ikke listen"); });
    }

    @POST
    @Path("/saksbehandler")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Legger til eller fjerner koblingen mellom saksliste og saksbehandler", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
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
