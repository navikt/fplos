package no.nav.foreldrepenger.los.tjenester.admin;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.los.tjenester.admin.driftsmelding.Driftsmelding;
import no.nav.foreldrepenger.los.tjenester.admin.driftsmelding.DriftsmeldingTjeneste;
import no.nav.foreldrepenger.los.tjenester.admin.dto.DriftsmeldingDto;
import no.nav.foreldrepenger.los.tjenester.admin.dto.DriftsmeldingOpprettelseDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/driftsmeldinger")
@Transactional
@ApplicationScoped
public class DriftsmeldingerAdminRestTjeneste {
    private DriftsmeldingTjeneste driftsmeldingTjeneste;

    @Inject
    public DriftsmeldingerAdminRestTjeneste(DriftsmeldingTjeneste driftsmeldingTjeneste) {
        this.driftsmeldingTjeneste = driftsmeldingTjeneste;
    }

    public DriftsmeldingerAdminRestTjeneste() {
    }

    @GET
    @Path("/aktive-driftsmeldinger")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Aktive driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public List<DriftsmeldingDto> hentAktiveDriftsmeldinger() {
        return driftsmeldingTjeneste.hentAktiveDriftsmeldinger().stream().map(DriftsmeldingerAdminRestTjeneste::tilDto).toList();
    }

    @GET
    @Path("/alle-driftsmeldinger")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Alle Driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public List<DriftsmeldingDto> hentAlleDriftsmeldinger() {
        return driftsmeldingTjeneste.hentAlleDriftsmeldinger().stream().map(DriftsmeldingerAdminRestTjeneste::tilDto).toList();
    }

    @POST
    @Path("/opprett")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opprett ny driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response opprettDriftsmelding(@Parameter(description = "jepp") @NotNull @Valid DriftsmeldingOpprettelseDto dto) {
        var melding = tilDriftsmelding(dto);
        driftsmeldingTjeneste.opprettDriftsmelding(melding);
        return Response.ok().build();
    }

    @POST
    @Path("/deaktiver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Deaktiver alle driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response deaktiverDriftsmeldinger() {
        driftsmeldingTjeneste.deaktiverDriftsmeldinger();
        return Response.ok().build();
    }

    private static DriftsmeldingDto tilDto(Driftsmelding driftsmelding) {
        return new DriftsmeldingDto(String.valueOf(driftsmelding.getId()), driftsmelding.getMelding(), driftsmelding.getAktivFra(),
            driftsmelding.getAktivTil());
    }

    private Driftsmelding tilDriftsmelding(DriftsmeldingOpprettelseDto dto) {
        return Driftsmelding.Builder.builder().medMelding(dto.getMelding()).medAktivFra(dto.getAktivFra()).medAktivTil(dto.getAktivTil()).build();
    }
}

