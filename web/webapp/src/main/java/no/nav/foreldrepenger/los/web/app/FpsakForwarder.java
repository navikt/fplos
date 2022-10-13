package no.nav.foreldrepenger.los.web.app;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import java.net.URI;

@ApplicationScoped
@Path("/")
public class FpsakForwarder {

    private static final Logger LOG = LoggerFactory.getLogger(FpsakForwarder.class);
    private URI fpsakUrl;


    public FpsakForwarder() {
    }

    @Inject
    public FpsakForwarder(@KonfigVerdi("fpsak.frontend.url") URI fpsakUrl) {
        this.fpsakUrl = fpsakUrl;
    }

    @GET
    public Response forwardFpsak() {
        LOG.info("Treff på /fplos, redirecter til {}", fpsakUrl);
        return Response.seeOther(fpsakUrl).build();
    }
}
