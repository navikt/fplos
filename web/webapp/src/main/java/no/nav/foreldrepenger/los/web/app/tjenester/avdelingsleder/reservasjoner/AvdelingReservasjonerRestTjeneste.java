package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.reservasjoner;

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
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveStatusDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.ReservasjonDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.fplos.domenetjenester.reservasjon.ReservasjonTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@Path("avdelingsleder/reservasjoner")
@ApplicationScoped
@Transactional
public class AvdelingReservasjonerRestTjeneste {

    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveDtoTjeneste oppgaveDtoTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public AvdelingReservasjonerRestTjeneste(ReservasjonTjeneste reservasjonTjeneste,
                                             OppgaveDtoTjeneste oppgaveDtoTjeneste,
                                             SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.oppgaveDtoTjeneste = oppgaveDtoTjeneste;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
    }

    public AvdelingReservasjonerRestTjeneste() {
        //CDI
    }
    @GET
    @Produces("application/json")
    @Operation(description = "Henter alle saksbehandlere", tags = "AvdelingslederReservasjoner")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<ReservasjonDto> hentAvdelingensReservasjoner(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        var reservasjoner = reservasjonTjeneste.hentReservasjonerForAvdeling(avdelingEnhetDto.getAvdelingEnhet());
        return tilReservasjonDtoListe(reservasjoner);
    }

    private List<ReservasjonDto> tilReservasjonDtoListe(List<Reservasjon> reservasjoner) {
        return reservasjoner.stream()
                .map(reservasjon -> {
                    var reservertAvNavn = saksbehandlerDtoTjeneste.hentSaksbehandlerNavn(reservasjon.getReservertAv());
                    return new ReservasjonDto(reservasjon, reservertAvNavn.orElse("Ukjent saksbehandler"), null);
                })
                .collect(Collectors.toList());
    }

    @POST
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opphev reservasjon av oppgave", tags = "AvdelingslederReservasjoner")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto opphevOppgaveReservasjon(@NotNull @Parameter(description = "Id for oppgave som reservasjonen er tilknyttet") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = reservasjonTjeneste.frigiOppgave(oppgaveId.getVerdi(), "Opphevet av avdelingsleder");
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }
}
