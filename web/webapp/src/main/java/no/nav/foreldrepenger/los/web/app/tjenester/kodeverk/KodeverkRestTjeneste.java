package no.nav.foreldrepenger.los.web.app.tjenester.kodeverk;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.jackson.JacksonJsonConfig;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/kodeverk")
@ApplicationScoped
public class KodeverkRestTjeneste {

    private HentKodeverkTjeneste hentKodeverkTjeneste; // NOSONAR

    private final JacksonJsonConfig jsonMapper = new JacksonJsonConfig(true); // generere fulle kodeverdi-objekt

    private final ObjectMapper objectMapper = jsonMapper.getObjectMapper();

    private String kodelisteCache;


    @Inject
    public KodeverkRestTjeneste(HentKodeverkTjeneste hentKodeverkTjeneste) {
        this.hentKodeverkTjeneste = hentKodeverkTjeneste;
    }

    public KodeverkRestTjeneste() {
        // for cdi
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter kodeliste", tags = "Kodeverk")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response hentGruppertKodeliste() throws IOException {
        var kodelisteJson = getKodeverkRawJson();
        return Response.ok()
            .entity(kodelisteJson)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private String getKodeverkRawJson() throws JsonProcessingException {
        if (kodelisteCache == null) {
            kodelisteCache = objectMapper.writeValueAsString(hentKodeverkTjeneste.hentGruppertKodeliste());
        }
        return kodelisteCache;
    }
}
