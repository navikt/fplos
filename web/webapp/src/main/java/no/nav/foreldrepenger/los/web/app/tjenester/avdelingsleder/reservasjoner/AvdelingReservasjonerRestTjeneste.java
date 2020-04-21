package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.reservasjoner;

import java.util.List;
import java.util.Optional;
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
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveStatusDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.ReservasjonDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.fplos.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

@Path("avdelingsleder/reservasjoner")
@ApplicationScoped
@Transactional
public class AvdelingReservasjonerRestTjeneste {

    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveDtoTjeneste oppgaveDtoTjeneste;
    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;

    @Inject
    public AvdelingReservasjonerRestTjeneste(OppgaveTjeneste oppgaveTjeneste,
                                             OppgaveDtoTjeneste oppgaveDtoTjeneste,
                                             AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.oppgaveDtoTjeneste = oppgaveDtoTjeneste;
        this.avdelingslederSaksbehandlerTjeneste = avdelingslederSaksbehandlerTjeneste;
    }

    public AvdelingReservasjonerRestTjeneste() {
        //CDI
    }
    @GET
    @Produces("application/json")
    @Operation(description = "Henter alle saksbehandlere", tags = "AvdelingslederReservasjoner")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<ReservasjonDto> hentAvdelingensReservasjoner(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        var reservasjoner = oppgaveTjeneste.hentReservasjonerForAvdeling(avdelingEnhetDto.getAvdelingEnhet());
        return tilReservasjonDtoListe(reservasjoner);
    }

    private List<ReservasjonDto> tilReservasjonDtoListe(List<Reservasjon> reservasjoner) {
        return reservasjoner.stream()
                .map(reservasjon -> {
                    var reservertAvNavn = hentSaksbehandlersNavn(reservasjon.getReservertAv());
                    return new ReservasjonDto(reservasjon, reservertAvNavn, null);
                })
                .collect(Collectors.toList());
    }

    private String hentSaksbehandlersNavn(String saksbehandlerIdent) {
        return Optional.ofNullable(avdelingslederSaksbehandlerTjeneste.hentSaksbehandlerNavn(saksbehandlerIdent))
                .map(String::toUpperCase)
                .orElse("UKJENT SAKSBEHANDLER");
    }

    @POST
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opphev reservasjon av oppgave", tags = "AvdelingslederReservasjoner")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto opphevOppgaveReservasjon(@NotNull @Parameter(description = "Id for oppgave som reservasjonen er tilknyttet") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = oppgaveTjeneste.frigiOppgave(oppgaveId.getVerdi(), "Opphevet av avdelingsleder");
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave()).getStatus();
    }
}
