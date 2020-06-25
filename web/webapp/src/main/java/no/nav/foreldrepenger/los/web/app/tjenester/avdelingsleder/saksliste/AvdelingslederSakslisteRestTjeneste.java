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
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
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
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@Path("avdelingsleder/sakslister")
@ApplicationScoped
@Transactional
public class AvdelingslederSakslisteRestTjeneste {

    private AvdelingslederTjeneste avdelingslederTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;

    @Inject
    public AvdelingslederSakslisteRestTjeneste(AvdelingslederTjeneste avdelingslederTjeneste, OppgaveTjeneste oppgaveTjeneste) {
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
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
        List<OppgaveFiltrering> filtersett = avdelingslederTjeneste.hentOppgaveFiltreringer(avdelingEnhet.getAvdelingEnhet());
        return filtersett.stream()
                .map(o-> new SakslisteDto(o,oppgaveTjeneste.hentAntallOppgaver(o.getId(), true)))
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
    @Path("/sortering-tidsintervall-dager")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sett sakslistens sorteringsintervall i dager", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervallDager(@NotNull @Parameter(description = "Sakslistens sortering gitt dagar") @Valid SakslisteSorteringIntervallDagerDto sakslisteSorteringIntervallDager) {
        avdelingslederTjeneste.settSorteringNumeriskIntervall(sakslisteSorteringIntervallDager.getSakslisteId(),
                sakslisteSorteringIntervallDager.getFra(),
                sakslisteSorteringIntervallDager.getTil());
    }

    @POST
    @Path("/sortering-tidsintervall-type")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sett sakslistens sorteringsintervall i dager", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreSorteringTidsintervallValg(@NotNull @Parameter(description = "id til sakslisten") @Valid SakslisteOgAvdelingDto sakslisteOgAvdelingDto) {
        OppgaveFiltrering oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteOgAvdelingDto.getSakslisteId().getVerdi());
        avdelingslederTjeneste.settSorteringTidsintervallValg(sakslisteOgAvdelingDto.getSakslisteId().getVerdi(),
                !oppgaveFiltrering.getErDynamiskPeriode());
    }

    @POST
    @Path("/saksbehandler")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Legger til eller fjerner koblingen mellom saksliste og saksbehandler", tags = "AvdelingslederSakslister")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void leggSaksbehandlerTilSaksliste(@NotNull @Parameter(description = "Knytning mellom saksbehandler og saksliste") @Valid SakslisteSaksbehandlerDto sakslisteSaksbehandler) {
        if(sakslisteSaksbehandler.isChecked()) {
            avdelingslederTjeneste.leggSaksbehandlerTilListe(sakslisteSaksbehandler.getSakslisteId(), sakslisteSaksbehandler.getBrukerIdent().getVerdi());
        }else{
            avdelingslederTjeneste.fjernSaksbehandlerFraListe(sakslisteSaksbehandler.getSakslisteId(), sakslisteSaksbehandler.getBrukerIdent().getVerdi());
        }
    }
}
