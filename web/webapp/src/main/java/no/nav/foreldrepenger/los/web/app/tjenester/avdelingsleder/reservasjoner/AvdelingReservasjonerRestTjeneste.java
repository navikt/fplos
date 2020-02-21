package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.reservasjoner;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
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

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET;

@Path("avdelingsleder/reservasjoner")
@RequestScoped
@Transactional
public class AvdelingReservasjonerRestTjeneste {

    private OppgaveTjeneste oppgaveTjeneste;

    public AvdelingReservasjonerRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public AvdelingReservasjonerRestTjeneste(OppgaveTjeneste oppgaveTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
    }
    @GET
    @Produces("application/json")
    @Operation(description = "Henter alle saksbehandlere", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<Reservasjon> hentAvdelingensReservasjoner(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return oppgaveTjeneste.hentReservasjonerForAvdeling(avdelingEnhetDto.getAvdelingEnhet());
    }

}
