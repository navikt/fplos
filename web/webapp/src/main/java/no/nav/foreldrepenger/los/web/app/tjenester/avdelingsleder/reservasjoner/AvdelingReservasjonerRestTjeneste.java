package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.reservasjoner;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.ReservasjonDto;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.fplos.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET;

@Path("avdelingsleder/reservasjoner")
@RequestScoped
@Transactional
public class AvdelingReservasjonerRestTjeneste {

    private OppgaveTjeneste oppgaveTjeneste;
    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;

    public AvdelingReservasjonerRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public AvdelingReservasjonerRestTjeneste(OppgaveTjeneste oppgaveTjeneste, AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.avdelingslederSaksbehandlerTjeneste = avdelingslederSaksbehandlerTjeneste;
    }
    @GET
    @Produces("application/json")
    @Operation(description = "Henter alle saksbehandlere", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<ReservasjonDto> hentAvdelingensReservasjoner(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        List<ReservasjonDto> reservasjoner = tilReservasjonDtoListe(oppgaveTjeneste.hentReservasjonerForAvdeling(avdelingEnhetDto.getAvdelingEnhet()));
        return reservasjoner;
    }

    private List<ReservasjonDto> tilReservasjonDtoListe(List<Reservasjon> reservasjoner) {
        List<ReservasjonDto> reservasjonDtoer = reservasjoner.stream().map(reservasjon -> new ReservasjonDto(
                reservasjon, hentSaksbehandlersNavn(reservasjon.getReservertAv()), null)).collect(Collectors.toList());
        return reservasjonDtoer;
    }

    private String hentSaksbehandlersNavn(String saksbehandlerIdent) {
        return Optional.ofNullable(avdelingslederSaksbehandlerTjeneste.hentSaksbehandlerNavn(saksbehandlerIdent))
                .map(String::toUpperCase)
                .orElse("UKJENT SAKSBEHANDLER");
    }

}
