package no.nav.foreldrepenger.los.web.app.tjenester;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.selftest.SelftestService;

@Path("/health")
@ApplicationScoped
public class NaisRestTjeneste {

    private static final String RESPONSE_CACHE_KEY = "Cache-Control";
    private static final String RESPONSE_CACHE_VAL = "must-revalidate,no-cache,no-store";
    private static final String RESPONSE_OK = "OK";

    private KafkaConsumerStarter kafkaConsumerStarter;
    private SelftestService selftestService;

    public NaisRestTjeneste() {
        // CDI
    }

    @Inject
    public NaisRestTjeneste(KafkaConsumerStarter kafkaConsumerStarter, SelftestService selftestService) {
        this.kafkaConsumerStarter = kafkaConsumerStarter;
        this.selftestService = selftestService;
    }

    @GET
    @Path("isAlive")
    @Operation(description = "sjekker om poden lever", tags = "nais", hidden = true)
    public Response isAlive() {
        return Response
                .ok(RESPONSE_OK)
                .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                .build();
    }

    @GET
    @Path("isReady")
    @Operation(description = "sjekker om poden er klar", tags = "nais", hidden = true)
    public Response isReady() {
        if (selftestService.kritiskTjenesteFeilet()) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        } else {
            return Response.ok(RESPONSE_OK)
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        }
    }

    @GET
    @Path("preStop")
    @Operation(description = "kalles på før stopp", tags = "nais", hidden = true)
    public Response preStop() {
        kafkaConsumerStarter.contextDestroyed(null);
        return Response.ok(RESPONSE_OK).build();
    }
}
