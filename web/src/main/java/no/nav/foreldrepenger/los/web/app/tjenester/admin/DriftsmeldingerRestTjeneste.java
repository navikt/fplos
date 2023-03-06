package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.admin.driftsmelding.Driftsmelding;
import no.nav.foreldrepenger.los.admin.driftsmelding.DriftsmeldingTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.DriftsmeldingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.DriftsmeldingOpprettelseDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/driftsmeldinger")
@Transactional
@ApplicationScoped
public class DriftsmeldingerRestTjeneste {
    private DriftsmeldingTjeneste driftsmeldingTjeneste;

    @Inject
    public DriftsmeldingerRestTjeneste(DriftsmeldingTjeneste driftsmeldingTjeneste) {
        this.driftsmeldingTjeneste = driftsmeldingTjeneste;
    }

    public DriftsmeldingerRestTjeneste() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public List<DriftsmeldingDto> hentAktiveDriftsmeldinger() {
        return driftsmeldingTjeneste.hentAktiveDriftsmeldinger().stream()
                .map(DriftsmeldingerRestTjeneste::tilDto)
                .toList();
    }

    @GET
    @Path("/alle-driftsmeldinger")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public List<DriftsmeldingDto> hentAlleDriftsmeldinger() {
        return driftsmeldingTjeneste.hentAlleDriftsmeldinger().stream()
                .map(DriftsmeldingerRestTjeneste::tilDto)
                .toList();
    }

    @POST
    @Path("/opprett")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response opprettDriftsmelding(@Parameter(description = "jepp") @NotNull @Valid DriftsmeldingOpprettelseDto dto) {
        var melding = tilDriftsmelding(dto);
        driftsmeldingTjeneste.opprettDriftsmelding(melding);
        return Response.ok().build();
    }

    @POST
    @Path("/deaktiver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response deaktiverDriftsmeldinger() {
        driftsmeldingTjeneste.deaktiverDriftsmeldinger();
        return Response.ok().build();
    }

    private static DriftsmeldingDto tilDto(Driftsmelding driftsmelding) {
        return new DriftsmeldingDto(String.valueOf(driftsmelding.getId()),
                driftsmelding.getMelding(), driftsmelding.getAktivFra(), driftsmelding.getAktivTil());
    }

    private Driftsmelding tilDriftsmelding(DriftsmeldingOpprettelseDto dto) {
        return Driftsmelding.Builder.builder()
                .medMelding(dto.getMelding())
                .medAktivFra(dto.getAktivFra())
                .medAktivTil(dto.getAktivTil())
                .build();
    }
}

