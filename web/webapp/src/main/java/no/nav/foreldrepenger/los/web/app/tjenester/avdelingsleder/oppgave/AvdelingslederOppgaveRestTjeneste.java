package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.oppgave;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET;

@Api(tags = "Avdelingsleder")
@Path("avdelingsleder/oppgaver")
@RequestScoped
@Transaction
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
    @Timed
    @Path("/antall")
    @Produces("application/json")
    @ApiOperation(value = "Henter antall oppgaver knyttet til sakslisten")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForSaksliste(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId,
                                                  @NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return oppgaveTjeneste.hentAntallOppgaver(sakslisteId.getVerdi());
    }

    @GET
    @Timed
    @Path("/avdelingantall")
    @Produces("application/json")
    @ApiOperation(value = "Henter antall oppgaver knyttet til avdelingen")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return oppgaveTjeneste.hentAntallOppgaverForAvdeling(avdelingEnhetDto.getAvdelingEnhet());
    }
}
