package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.reservasjoner;

import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.RESERVASJON_AVSLUTTET_AVDELINGSLEDER;

import java.util.List;

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
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.ReservasjonDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.vedtak.felles.jpa.TomtResultatException;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("avdelingsleder/reservasjoner")
@ApplicationScoped
@Transactional
public class AvdelingReservasjonerRestTjeneste {

    private ReservasjonTjeneste reservasjonTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public AvdelingReservasjonerRestTjeneste(ReservasjonTjeneste reservasjonTjeneste,
                                             SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
    }

    public AvdelingReservasjonerRestTjeneste() {
        //CDI
    }
    @GET
    @Produces("application/json")
    @Operation(description = "Henter alle saksbehandlere", tags = "AvdelingslederReservasjoner")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<ReservasjonDto> hentAvdelingensReservasjoner(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        var reservasjoner = reservasjonTjeneste.hentReservasjonerForAvdeling(avdelingEnhetDto.getAvdelingEnhet());
        return tilReservasjonDtoListe(reservasjoner);
    }

    private List<ReservasjonDto> tilReservasjonDtoListe(List<Reservasjon> reservasjoner) {
        return reservasjoner.stream()
                .map(reservasjon -> {
                    var reservertAvNavn = saksbehandlerDtoTjeneste.hentSaksbehandlerNavn(reservasjon.getReservertAv())
                            .orElseGet(() -> "Ukjent saksbehandler " + reservasjon.getReservertAv());
                    return new ReservasjonDto(reservasjon, reservertAvNavn, null);
                })
                .toList();
    }

    @POST
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Opphev reservasjon av oppgave", tags = "AvdelingslederReservasjoner")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public Response opphevOppgaveReservasjon(@NotNull @Parameter(description = "Id for oppgave som reservasjonen er tilknyttet") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = reservasjonTjeneste.slettReservasjonMedEventLogg(oppgaveId.getVerdi(), RESERVASJON_AVSLUTTET_AVDELINGSLEDER);
        if (reservasjon.isEmpty()) {
            throw new TomtResultatException("FPLOS-AVDL1", "Fant ikke reservasjon (eller reservasjon utløpt)");
        }
        return Response.noContent().build();
    }
}
