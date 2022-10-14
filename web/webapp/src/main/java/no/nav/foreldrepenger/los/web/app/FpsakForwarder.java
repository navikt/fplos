package no.nav.foreldrepenger.los.web.app;

import no.nav.foreldrepenger.konfig.KonfigVerdi;

import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import java.net.URI;


@ApplicationScoped
@Path("/")
public class FpsakForwarder {

    private static final Logger LOG = LoggerFactory.getLogger(FpsakForwarder.class);
    private URI avdelingslederFpsakUri;
    private URI fpsakUri;


    public FpsakForwarder() {
    }

    @Inject
    public FpsakForwarder(@KonfigVerdi("fpsak.frontend.url") URI fpsakUri) {
        this.fpsakUri = fpsakUri;
        this.avdelingslederFpsakUri = URI.create(fpsakUri.toString() + "/avdelingsleder");
    }

    @GET
    public Response forwardFpsak(@Context HttpServletRequest request) {
        var rawUri = ((Request) request).getMetaData().getURIString();
        if (rawUri.contains("avdelingsleder")) {
            LOG.info("Treff på /fplos/avdelingsleder, redirecter til {}", avdelingslederFpsakUri);
            return Response.seeOther(avdelingslederFpsakUri).build();
        }
        LOG.info("Treff på /fplos, redirecter til {}", fpsakUri);
        return Response.seeOther(fpsakUri).build();
    }
}
