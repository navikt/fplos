package no.nav.foreldrepenger.los.tjenester.avdelingsleder.reservasjon;

import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.RESERVASJON_AVSLUTTET_AVDELINGSLEDER;

import java.util.List;

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
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.ReservasjonDto;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.vedtak.felles.jpa.TomtResultatException;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("avdelingsleder/reservasjoner")
@ApplicationScoped
@Transactional
public class AvdelingReservasjonerRestTjeneste {

    private ReservasjonTjeneste reservasjonTjeneste;
    private AnsattTjeneste ansattTjeneste;

    @Inject
    public AvdelingReservasjonerRestTjeneste(ReservasjonTjeneste reservasjonTjeneste, AnsattTjeneste ansattTjeneste) {
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.ansattTjeneste = ansattTjeneste;
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
            var reservertAvNavn = ansattTjeneste.hentBrukerProfilForLagretSaksbehandler(reservasjon.getReservertAv())
                .map(BrukerProfil::navn)
                .orElseGet(() -> "Ukjent saksbehandler " + reservasjon.getReservertAv());
            return new ReservasjonDto(reservasjon, reservertAvNavn, null);
        }).toList();
    }

    @POST
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Opphev reservasjon av oppgave", tags = "AvdelingslederReservasjoner")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public Response opphevOppgaveReservasjon(@NotNull @Parameter(description = "Id for oppgave som reservasjonen er tilknyttet") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = reservasjonTjeneste.slettReservasjonMedEventLogg(oppgaveId.getVerdi(), RESERVASJON_AVSLUTTET_AVDELINGSLEDER);
        if (reservasjon.isEmpty()) {
            throw new TomtResultatException("FPLOS-AVDL1", "Fant ikke reservasjon (eller reservasjon utløpt)");
        }
        return Response.noContent().build();
    }
}
