package no.nav.foreldrepenger.los.web.app.tjenester;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.selftest.checks.DatabaseHealthCheck;

@Path("/health")
@ApplicationScoped
public class NaisRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(NaisRestTjeneste.class);

    private static final String RESPONSE_CACHE_KEY = "Cache-Control";
    private static final String RESPONSE_CACHE_VAL = "must-revalidate,no-cache,no-store";
    private static final String RESPONSE_OK = "OK";

    private KafkaConsumerStarter kafkaConsumerStarter;
    private DatabaseHealthCheck databaseHealthCheck;

    public NaisRestTjeneste() {
        // CDI
    }

    @Inject
    public NaisRestTjeneste(KafkaConsumerStarter kafkaConsumerStarter, DatabaseHealthCheck databaseHealthCheck) {
        this.kafkaConsumerStarter = kafkaConsumerStarter;
        this.databaseHealthCheck = databaseHealthCheck;
    }

    @GET
    @Path("isAlive")
    @Operation(description = "sjekker om poden lever", tags = "nais", hidden = true)
    public Response isAlive() {
        if (kafkaConsumerStarter.isConsumersRunning()) {
            return Response.ok(RESPONSE_OK)
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        }
        return Response.serverError()
                .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                .build();
    }

    @GET
    @Path("isReady")
    @Operation(description = "sjekker om poden er klar", tags = "nais", hidden = true)
    public Response isReady() {
        if (kafkaConsumerStarter.isConsumersRunning() && databaseHealthCheck.isReady()) {
            return Response.ok(RESPONSE_OK)
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                .build();
    }

    @GET
    @Path("preStop")
    @Operation(description = "kalles på før stopp", tags = "nais", hidden = true)
    public Response preStop() {
        LOG.info("preStop endepunkt kalt");
        kafkaConsumerStarter.destroy();
        return Response.ok(RESPONSE_OK).build();
    }
}
