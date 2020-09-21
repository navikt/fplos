package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.function.Function;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;


@Path("/prosesstask-forvaltning")
@ApplicationScoped
@Transactional
public class AdminProsesstaskRestTjeneste {

    private static final Logger logger = LoggerFactory.getLogger(AdminProsesstaskRestTjeneste.class);

    private ProsessTaskRepository prosessTaskRepository;

    public AdminProsesstaskRestTjeneste() {
        // For CDI
    }

    @Inject
    public AdminProsesstaskRestTjeneste(ProsessTaskRepository prosessTaskRepository) {
        this.prosessTaskRepository = prosessTaskRepository;
    }

    @POST
    @Path("/sett-task-ferdig")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(description = "Setter prosesstask til status FERDIG",
            tags = "Prosesstask-forvaltning",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task satt til ferdig."),
                    @ApiResponse(responseCode = "400", description = "Fant ikke aktuell prosessTask."),
                    @ApiResponse(responseCode = "500", description = "Feilet pga ukjent feil.")
            })
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response setTaskFerdig(@TilpassetAbacAttributt(supplierClass = AdminProsesstaskRestTjeneste.AbacDataSupplier.class)
                                  @Parameter(description = "Task som skal settes ferdig") @NotNull @Valid ProsessTaskIdDto taskId) {
        ProsessTaskData data = prosessTaskRepository.finn(taskId.getProsessTaskId());
        if (data != null) {
            data.setStatus(ProsessTaskStatus.FERDIG);
            data.setSisteFeil(null);
            data.setSisteFeilKode(null);
            prosessTaskRepository.lagre(data);
            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    public static class AbacDataSupplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            return AbacDataAttributter.opprett();
        }
    }
}
