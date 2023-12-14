package no.nav.foreldrepenger.los.tjenester.avdelingsleder.reservasjoner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.ReservasjonDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.vedtak.felles.jpa.TomtResultatException;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.RESERVASJON_AVSLUTTET_AVDELINGSLEDER;

@Path("avdelingsleder/reservasjoner")
@ApplicationScoped
@Transactional
public class AvdelingReservasjonerRestTjeneste {

    private ReservasjonTjeneste reservasjonTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public AvdelingReservasjonerRestTjeneste(ReservasjonTjeneste reservasjonTjeneste, SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
    }

    public AvdelingReservasjonerRestTjeneste() {
        //CDI
    }

    @GET
    @Produces("application/json")
    @Operation(description = "Henter alle reservasjoner", tags = "AvdelingslederReservasjoner")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<ReservasjonDto> hentAvdelingensReservasjoner(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        var reservasjoner = reservasjonTjeneste.hentReservasjonerForAvdeling(avdelingEnhetDto.getAvdelingEnhet());
        return tilReservasjonDtoListe(reservasjoner);
    }

    private List<ReservasjonDto> tilReservasjonDtoListe(List<Reservasjon> reservasjoner) {
        return reservasjoner.stream().map(reservasjon -> {
            var reservertAvNavn = saksbehandlerDtoTjeneste.hentSaksbehandlerNavn(reservasjon.getReservertAv())
                .orElseGet(() -> "Ukjent saksbehandler " + reservasjon.getReservertAv());
            return new ReservasjonDto(reservasjon, reservertAvNavn, null);
        }).toList();
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
