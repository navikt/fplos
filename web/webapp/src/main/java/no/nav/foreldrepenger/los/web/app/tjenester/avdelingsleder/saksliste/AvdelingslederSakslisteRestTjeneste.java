package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteAndreKriterierDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteBehandlingstypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteFagsakYtelseTypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteNavnDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringIntervallDagerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSorteringIntervallDatoDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

@Api(tags = "Avdelingsleder")
@Path("avdelingsleder/sakslister")
@RequestScoped
@Transaction
public class AvdelingslederSakslisteRestTjeneste {

    private AvdelingslederTjeneste avdelingslederTjeneste;


    @Inject
    public AvdelingslederSakslisteRestTjeneste(AvdelingslederTjeneste avdelingslederTjeneste) {
        this.avdelingslederTjeneste = avdelingslederTjeneste;
    }

    public AvdelingslederSakslisteRestTjeneste() {
        //NOSONAR
    }

    @GET
    @Timed
    @Produces("application/json")
    @ApiOperation(value = "Henter alle sakslister for avdeling")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<SakslisteDto> hentAvdelingensSakslister(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        List<OppgaveFiltrering> filtersett = avdelingslederTjeneste.hentOppgaveFiltreringer(avdelingEnhet.getAvdelingEnhet());
        return filtersett.stream()
                .map(SakslisteDto::new)
                .collect(Collectors.toList());
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Lag ny saksliste", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public SakslisteIdDto opprettNySaksliste(@NotNull @ApiParam("enhet til avdelingsenheten som det skal opprettes ny saksliste for") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return new SakslisteIdDto(avdelingslederTjeneste.lagNyOppgaveFiltrering(avdelingEnhetDto.getAvdelingEnhet()));
    }

    //FIXME (TOR) burde ein heller brukt @DELETE her?
    @POST
    @Timed
    @Path("/slett")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Fjern saksliste", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING)
//    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void slettSaksliste(@NotNull @ApiParam("id til sakslisten som skal slettes") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        avdelingslederTjeneste.slettOppgaveFiltrering(sakslisteOgAvdelingDto.getSakslisteId().getVerdi());
    }

    @POST
    @Timed
    @Path("/navn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Lagre sakslistens navn", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreNavn(@NotNull @ApiParam("Sakslistens navn") @Valid SakslisteNavnDto sakslisteNavn) {
        avdelingslederTjeneste.giListeNyttNavn(sakslisteNavn.getSakslisteId(),sakslisteNavn.getNavn());
    }

    @POST
    @Timed
    @Path("/behandlingstype")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Lagre sakslistens behandlingstype", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreBehandlingstype(@NotNull @ApiParam("Sakslistens behandlingstype") @Valid SakslisteBehandlingstypeDto sakslisteBehandlingstype) {
        avdelingslederTjeneste.endreFiltreringBehandlingType(sakslisteBehandlingstype.getSakslisteId()
                ,sakslisteBehandlingstype.getBehandlingType(), sakslisteBehandlingstype.isChecked());
    }

    @POST
    @Timed
    @Path("/ytelsetype")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Lagre sakslistens behandlingstype", notes = ("sakslisteFagsakYtelseTypeDto.getSakslisteId() kan være null. Dette vil si at det ikke skal være noen filtrering på "))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreFagsakYtelseType(@NotNull @ApiParam("Sakslistens ytelsetype") @Valid SakslisteFagsakYtelseTypeDto sakslisteFagsakYtelseTypeDto) {
        avdelingslederTjeneste.endreFiltreringYtelseType(
                sakslisteFagsakYtelseTypeDto.getSakslisteId(),
                sakslisteFagsakYtelseTypeDto.getFagsakYtelseType());
    }

    @POST
    @Timed
    @Path("/andre-kriterier")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Lagre sakslistens 'Andre kriterier'")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreAndreKriterierType(@NotNull @ApiParam("Sakslistens 'andre kriterier'") @Valid SakslisteAndreKriterierDto sakslisteAndreKriterierDto) {
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(
                    sakslisteAndreKriterierDto.getSakslisteId(),
                    sakslisteAndreKriterierDto.getAndreKriterierType(),
                    sakslisteAndreKriterierDto.isChecked(),
                    sakslisteAndreKriterierDto.isInkluder());
    }

    @POST
    @Timed
    @Path("/sortering")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Sett sakslistens sortering", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSortering(@NotNull @ApiParam("Sakslistens sortering") @Valid SakslisteSorteringDto sakslisteSortering) {
        avdelingslederTjeneste.settSortering(sakslisteSortering.getSakslisteId(),
                sakslisteSortering.getSakslisteSorteringValg());
    }

    @POST
    @Timed
    @Path("/sortering-tidsintervall-dato")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Sett sakslistens sorteringintervall ved start og slutt datoer", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervallDato(@NotNull @ApiParam("Sakslistens sorteringsintervall gitt datoer") @Valid SakslisteSorteringIntervallDatoDto sakslisteSorteringIntervallDato) {
        avdelingslederTjeneste.settSorteringTidsintervallDato(sakslisteSorteringIntervallDato.getSakslisteId(),
                sakslisteSorteringIntervallDato.getFomDato(),
                sakslisteSorteringIntervallDato.getTomDato());
    }

    @POST
    @Timed
    @Path("/sortering-tidsintervall-dager")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Sett sakslistens sorteringsintervall i dager", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervallDager(@NotNull @ApiParam("Sakslistens sortering gitt dagar") @Valid SakslisteSorteringIntervallDagerDto sakslisteSorteringIntervallDager) {
        avdelingslederTjeneste.settSorteringTidsintervallDager(sakslisteSorteringIntervallDager.getSakslisteId(),
                sakslisteSorteringIntervallDager.getFomDager(),
                sakslisteSorteringIntervallDager.getTomDager());
    }

    @POST
    @Timed
    @Path("/sortering-tidsintervall-type")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Sett sakslistens sorteringsintervall i dager", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervallValg(@NotNull @ApiParam("id til sakslisten") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        OppgaveFiltrering oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteOgAvdelingDto.getSakslisteId().getVerdi());
        avdelingslederTjeneste.settSorteringTidsintervallValg(sakslisteOgAvdelingDto.getSakslisteId().getVerdi(),
                !oppgaveFiltrering.getErDynamiskPeriode());
    }

    @POST
    @Timed
    @Path("/saksbehandler")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Legger til eller fjerner koblingen mellom saksliste og saksbehandler", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void leggSaksbehandlerTilSaksliste(@NotNull @ApiParam("Knytning mellom saksbehandler og saksliste") @Valid SakslisteSaksbehandlerDto sakslisteSaksbehandler) {
        if(sakslisteSaksbehandler.isChecked()) {
            avdelingslederTjeneste.leggSaksbehandlerTilListe(sakslisteSaksbehandler.getSakslisteId(), sakslisteSaksbehandler.getBrukerIdent().getVerdi());
        }else{
            avdelingslederTjeneste.fjernSaksbehandlerFraListe(sakslisteSaksbehandler.getSakslisteId(), sakslisteSaksbehandler.getBrukerIdent().getVerdi());
        }
    }
}
