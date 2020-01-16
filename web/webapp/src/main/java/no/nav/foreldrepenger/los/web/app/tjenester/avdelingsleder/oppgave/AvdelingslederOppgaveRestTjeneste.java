package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.oppgave;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
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

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET;

@Path("avdelingsleder/oppgaver")
@RequestScoped
@Transactional
public class AvdelingslederOppgaveRestTjeneste {

    private OppgaveTjeneste oppgaveTjeneste;

    public AvdelingslederOppgaveRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public AvdelingslederOppgaveRestTjeneste(OppgaveTjeneste oppgaveTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    @GET
    @Path("/antall")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til sakslisten", tags = "AvdelingslederOppgaver")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForSaksliste(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId,
                                                  @NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return oppgaveTjeneste.hentAntallOppgaver(sakslisteId.getVerdi(), true);
    }

    @GET
    @Path("/avdelingantall")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til avdelingen", tags = "AvdelingslederOppgaver")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return oppgaveTjeneste.hentAntallOppgaverForAvdeling(avdelingEnhetDto.getAvdelingEnhet());
    }
}
