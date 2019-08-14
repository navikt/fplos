package no.nav.foreldrepenger.los.web.app.tjenester;

import io.swagger.annotations.Api;
import no.nav.foreldrepenger.los.web.app.selftest.SelftestService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Api(tags = {"Helsesjekker"})
@Path("/selftest")
@RequestScoped
public class SelftestRestTjeneste {

    private SelftestService selftestService;

    public SelftestRestTjeneste() {
        // CDI
    }

    @Inject
    public SelftestRestTjeneste(SelftestService selftestService) {
        this.selftestService = selftestService;
    }

    @GET
    @Produces({TEXT_HTML, APPLICATION_JSON})
    public Response doSelftest(@HeaderParam("Content-Type") String contentType, @QueryParam("json") boolean writeJsonAsHtml) {
        return selftestService.doSelftest(contentType, writeJsonAsHtml);
    }


}