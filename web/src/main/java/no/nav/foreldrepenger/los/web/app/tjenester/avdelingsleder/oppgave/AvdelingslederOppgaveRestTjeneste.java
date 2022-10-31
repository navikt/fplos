package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.oppgave;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("avdelingsleder/oppgaver")
@ApplicationScoped
@Transactional
public class AvdelingslederOppgaveRestTjeneste {

    private OppgaveKøTjeneste oppgaveKøTjeneste;

    public AvdelingslederOppgaveRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public AvdelingslederOppgaveRestTjeneste(OppgaveKøTjeneste oppgaveKøTjeneste) {
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
    }

    @GET
    @Path("/antall")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til sakslisten", tags = "AvdelingslederOppgaver")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForSaksliste(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId,
                                                  @NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return oppgaveKøTjeneste.hentAntallOppgaver(sakslisteId.getVerdi(), true);
    }

    @GET
    @Path("/avdelingantall")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til avdelingen", tags = "AvdelingslederOppgaver")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return oppgaveKøTjeneste.hentAntallOppgaverForAvdeling(avdelingEnhetDto.getAvdelingEnhet());
    }
}
