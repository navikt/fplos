package no.nav.foreldrepenger.los.tjenester.kodeverk;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.felles.Kodeverdi;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.konfig.JacksonJsonConfig;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.BehandlingVenteStatus;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

import java.util.HashMap;
import java.util.Map;

@Path("/kodeverk")
@ApplicationScoped
public class KodeverkRestTjeneste {

    private static final String KODELISTE_JSON = jsonKodeverk();

    KodeverkRestTjeneste() {
        // for cdi
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter kodeliste", tags = "Kodeverk")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public Response hentGruppertKodeliste() {
        return Response.ok().entity(KODELISTE_JSON).type(MediaType.APPLICATION_JSON).build();
    }

    private static String jsonKodeverk() {
        var jsonMapper = new JacksonJsonConfig(true); // generere fulle kodeverdi-objekt
        var mapper = jsonMapper.getObjectMapper();
        try {
            return mapper.writeValueAsString(gruppertKodeliste());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Kunne ikke generere kodeliste");
        }
    }

    private static Map<String, Kodeverdi[]> gruppertKodeliste() {
        var map = new HashMap<String, Kodeverdi[]>();
        map.put(BehandlingType.class.getSimpleName(), BehandlingType.values());
        map.put(FagsakYtelseType.class.getSimpleName(), FagsakYtelseType.values());
        map.put(KøSortering.class.getSimpleName(), KøSortering.values());
        map.put(FagsakStatus.class.getSimpleName(), FagsakStatus.values());
        map.put(AndreKriterierType.class.getSimpleName(), AndreKriterierType.values());
        map.put(BehandlingVenteStatus.class.getSimpleName(), BehandlingVenteStatus.values());
        return map;
    }

}
